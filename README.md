# bekit工具箱
本来想叫框架，但现在还上升不到那样的高度，就暂时称之为工具箱。本工具箱致力于解决在应用开发中公共性的问题，目前只有流程引擎一个功能，后续会陆续增加其他功能。
> 目前本工具还没有传到maven中央库（这两天会研究下怎么搞上去），所以你现在要使用的话需要先把代码拉到你本地，然后执行maven命令安装到你本地（mvn clean install）

##流程引擎
流程引擎比较适合订单类业务场景，功能包含流程编排和事务控制。
> 诞生原因：在订单类业务场景中，一条订单从创建到最后完成期间，往往会经历很多个状态，状态我将之称为节点，状态和状态的流转我将它称之为流程。我们往往是将流程嵌入到处理过程代码中。
这样做的缺点就是会将处理过程和流程耦合在一起，即不方便查看整个订单的流程，也不方便维护处理过程。
当一种新类型的订单需要相同的处理过程时，那么这个处理过程里面就嵌入了两种流程，这样就更不好维护了。
为了解决这种问题，我们势必要将流程从处理过程中剥离出来。
但是流程通过什么形式来定义，以及每个节点和它对应的处理过程怎么进行关联，还有整个过程中什么时候应该新启事务，什么时候应该提交事务。
为了解决这些问题，流程引擎就有了存在的价值。

####将流程引擎引入进你的系统：
1. 引入流程引擎依赖

        <dependency>
            <groupId>top.bekit</groupId>
            <artifactId>flow</artifactId>
            <version>1.0</version>
        </dependency>

2. 如果是spring-boot项目则需要在application.properties中加入配置：

        bekit.flow.enable=true

    如果是非spring-boot项目则需要手动引入流程引擎配置类FlowEngineConfiguration，比如：

        @Configuration
        @Import(FlowEngineConfiguration.class)
        public class MyImport {
        }
3. 在需要使用流程引擎的地方注入FlowEngine，然后就可以调用FlowEngine里面的方法，例如：

        @Autowired
        private FlowEngine flowEngine;

####一个简单的流程定义样例：
1. 定义流程

        @Flow  // 流程注解
        public class DemoFlow {
        
            @StartNode      // 开始节点
            public String start() {
                return "node1";
            }
        
            @ProcessNode(processor = "node1Processor")      // 处理节点（一种节点类型）
            public String node1(String processResult) {     // processResult类型需要满足能被处理器返回值进行赋值就可以
                // 根据处理器返回结果判断需要执行的下一个节点
                // 这里为简单演示，直接返回下一个节点
                return "node2";
            }
        
            @StateNode(processor = "node2Processor")        // 状态节点
            public String node2(String processResult) {
                return "node3";
            }
        
            @WaitNode(processor = "node3Processor")         // 等待节点
            public String node3(){
                // 也可以没有processResult入参
                return "node4";
            }
        
            @ProcessNode(processor = "node4Processor")
            public String node4(String processResult) {
                return "end";
            }
        
            @EndNode        // 结束节点
            public void end() {
            }
            
            @TargetMapping      // 目标对象映射
            public String targetMapping(Object target){
                // 将目标对象（比如订单）映射到需要执行的节点
                if (XXX){
                    return "start";
                }else if (XXX){
                    return "node3";
                }else if (XXX){
                    return "node4";
                }else {
                    return "end";
                }
            }
        }
 
    流程通过@Flow注解标识，流程中的节点分为开始节点（@StartNode）、处理节点（@ProcessNode）、状态节点（@StateNode）、等待节点（@WaitNode）、结束节点（@EndNode）
 这几种类型，每个流程都必须有一个开始节点和一个结束节点，处理节点处理完成后不会提交事务，而状态节点处理完后会提交事务并再新启一个事务，当自动执行到等待节点时（还未执行）流程会自动停止，如果需要执行等待节点，则需要手动的调用流程引擎（这符合像等待异步通知这类场景）。
 流程在开始前会自动的开启一个新事务，流程正常执行结束后（无异常抛出），则会提交事务，否则会回滚事务（如果前面有状态节点，则已经提交的哪些事务是回滚不了的。要的就是这个效果）。
 除了开始节点和结束节点，每个节点都可以通过processor属性指定一个处理器（真正干活的，就是前面说的处理过程）。
 每个流程还必须有一个目标对象映射，它是用来让流程引擎知道应该从哪个节点开始执行。
> @Flow注解有个属性enableFlowTx，它是用来控制是否开启流程事务，默认是开启。如果设置为false，则流程引擎不会在事务上做任何控制（既不会开启事务，也不会关闭事务），当然下面要介绍的@FlowTx注解你也不需要使用了。
 
2. 处理器

        @Processor
        public class Node1Processor {
            
            @Before  // 业务前置处理，可选    
            public void before(TargetContext targetContext) {
                // 可以做一些检查之类的操作
            }
        
            @Execute  // 业务处理，必选
            public String execute(TargetContext targetContext) {
                // 这个是处理器的主体方法，一般就是真正干活的方法，它的返回值会成为整个处理器的返回值，然后传到节点对应的方法
                return "success";
            }
        
            @After  // 业务后置处理，可选
            public void after(TargetContext targetContext) {
                // execute执行完后执行，可以做一些结果校验之类的
            }
        
            @End    // 业务结束处理，可选
            public void end(TargetContext targetContext) {
                // 最后执行（即使发生异常也会执行）
            }
        
            @Error   // 异常处理，可选
            public void error(TargetContext targetContext) {
                // 当before、execute、after任何一个发生异常后会执行
            }
        }

    处理器通过@Processor进行注解，处理器中必须得有@Execute方法，处理器方法可以没有入参，也可以有入参（入参类型必须是TargetContext）
处理器执行过程中发生任何异常都会往外抛。
一个处理器可以同时被多个流程使用。

3. 流程监听器

        @FlowListener(flow = "demoFlow")
        public class DenoFlowListener {
        
            @ListenDecideNode(nodeExpression = "node1")     // 监听选择节点事件，nodeExpression是需要被监听节点的正则表达式
            public void listenNode1(String node, TargetContext targetContext) {
                // 一般是更新目标对象状态
                // 本方法只有在被选择节点是node1时才会执行
            }
        
            @ListenDecideNode(nodeExpression = ".*")     // 监听选择节点事件
            public void listenAllNode(String node, TargetContext targetContext) {
                // 本方法在所有节点被选择是都会执行
            }
        }

    流程监听器可以监听对应流程发生的事件。目前只有节点被选择事件，此事件是要是用来更新目标对象状态的，实现更新状态和流程分离。

4. 流程事务

        @FlowTx(flow = "demoFlow")
        public class DemoFlowTx {
        
            @LockTarget     // 锁目标对象，必选
            public Trade lockTarget(TargetContext targetContext) {
                // 锁目标对象具体代码
                // 必须返回被锁后的目标对象
            }
        
            @InsertTarget   // 插入目标对象，可选
            public Trade insertTarget(TargetContext targetContext) {
                // 插入目标对象到数据库具体代码
                // 比需返回插入后的目标对象
            }
        }

    流程事务必须包括锁目标对象方法，流程在开始执行前会先开启一个新事务同时立马调用@LockTarget方法锁住目标对象，当遇到状态节点执行完时会提交事务，接着会再开启新事务并调用@LockTarget方法锁住目标对象。

    插入目标对象方法（@InsertTarget）的作用就是开启一个新事务用来插入目标对象到数据库并提交。
存在原因：如果在调用流程引擎前自行将目标对象插入到数据库但未提交，而流程引擎是新启事务后再锁对象，这样会导致流程引擎锁不到目标对象。所以流程引擎留一个口子专门新启一个事务让用户来插入目标对象。
>特别注意：自己先把目标对象锁了，再调用流程引擎，那么流程引擎在锁目标对象时就会出现死锁，这个千万要注意！！！
