# bekit框架
1. 简介

> 本框架致力于解决在应用开发中公共性问题，规范开发者代码，节省开发者时间，让开发者把更多精力放在自己的业务上。目前已完成“服务引擎”、“事件总线”、“流程引擎”功能模块，后续会陆续增加其他功能。

2. 环境要求：

> * jdk1.8
> * Spring 4.2及以上


> 注意：本框架已经上传到maven中央库

3. 感谢

> 感谢大神@李根的ADK框架，很多灵感和想法是从ADK上获取到的。

## 1. 服务引擎
服务引擎用于规范服务执行步骤，解决服务执行中常见公共性痛点。
> 诞生原因：当一个系统需要提供服务给上层系统调用时，往往服务会进行一些固定步骤操作，比如：入参校验（像JSR303）、业务参数校验、开启事务（如果有必要）、执行业务、异常捕获、构建应答。如果每个服务都需要自己来编排这些步骤显然是没有必要的，故此服务引擎就有了存在的意义。

### 1. 将服务引擎引入进你的系统

1. 引入服务引擎依赖

        <dependency>
            <groupId>top.bekit</groupId>
            <artifactId>service</artifactId>
            <version>1.0.0.RELEASE</version>
        </dependency>

2. 如果是spring-boot项目则只需要在application.properties中加入配置：

        bekit.service.enable=true

    如果是非spring-boot项目则需要手动引入服务引擎配置类ServiceEngineConfiguration，比如：
    
        @Configuration
        @Import(ServiceEngineConfiguration.class)
        public class MyImport {
        }

3. 在需要执行服务的地方注入服务引擎ServiceEngine：

        @Autowired
        private ServiceEngine serviceEngine;

    然后就可以执行服务：
    
        // myService是需要执行的服务名称，XXXOrder是这个服务需要的order，XXXResult是这个服务的result（在执行过程中你肯定会对result设值）
        XXXResult result = serviceEngine.execute("myService", new XXXOrder("001"), new XXXResult());
        
### 2. 一个简单的服务引擎使用样例：
一个完整的对服务引擎的使用应该包括：定义服务、定义服务监听器、使用服务引擎执行服务。

#### 1. 定义服务

        @Service(enableTx = true) // 服务定义注解（注意：此注解和spring的@Service注解名字一样，但是是两个不一样的注解），enableTx属性表示服务是否开启事务，默认不开启
        public class MyService {
            @ServiceCheck // 服务校验注解，进行业务校验（注意：此注解对应方法不会开启事务，原因是这里面只是进行业务校验，而不是执行业务，所以没必要开启事务；同时如果开启了事务，但是当校验不通过还需要回滚事务，反而浪费资源）
            public void check(ServiceContext<XXXOrder, XXXResult> serviceContext) { 
                // ServiceContext是服务上下文，可以通过它获取到传给服务引擎的order和result
                // 进行业务校验（比如校验账户是否合法等等）
            }
        
            @ServiceExecute // 服务执行注解，即真正开始执行业务（注意：@Service的enableTx属性为true的话，会在进入本方法前开启新事务，如果没有异常抛出，则提交事务，如果有任何异常抛出，则回滚事务）
            public void execute(ServiceContext<XXXOrder, XXXResult> serviceContext) {
                // 真正执行业务
            }
        }

> 被服务引擎的@Service注解的服务会被自动注册到Spring容器中（因为它继承了spring的@Component），它里面有个name属性用于指定服务名称，默认使用被注解的类名（首字母小写）。如果这个服务需要多次提交事务（比如订单类业务），强烈建议结合另一个功能模块“流程引擎”一起使用，它会让程序更简单。

#### 2. 定义服务监听器
服务监听器就是专门监听服务引擎发出的事件。

        @ServiceListener(priority = 1)  // 服务监听器注解（专门监听服务引擎发出的事件），priority属性表示监听器优先级（具体使用方式请查看“事件总线”功能模块）
        public class MyServiceListener {
            @Listen // 监听注解（具体使用方式请查看“事件总线”功能模块）
            public void listenServiceApplyEvent(ServiceApplyEvent event) {
                // ServiceApplyEvent是服务申请事件，表示即将要执行某个服务
                // 你监听这个事件后往往会进行某些初始化，比如：初始化result，对日志的MDC设值等等，具体看你的业务
            }
        
            @Listen(priorityAsc = false) // priorityAsc属性表示是否优先级升序（具体使用方式请查看“事件总线”功能模块）
            public void listenServiceFinishEvent(ServiceFinishEvent event) {
                // ServiceFinishEvent是服务完成事件，表示服务已经执行完了（注意：不管服务是否有异常往外抛，都会发布这个事件）
                // 你监听这个事件后往往会进行某些结尾性操作，比如：根据某些条件对result设值，移除MDC中的数据等等
                
                // 关于这个事件如果是我的话，我就会根据线程变量里的值对result设置，因为我所在的公司要求每个result都需要继承一个公共的基础result，
                // 这个基础result里包含：status（成功、失败、处理中）、code（结果码）、description（结果描述），
                // 如果在服务执行过程中对这个基础result设值，我觉得很累赘，代码一点也不美观
                // 那么我就把这三个数据放到线程变量里，然后在这个事件里统一对基础result设值。
            }
        
            @Listen
            public void catchServiceExceptionEvent(ServiceExceptionEvent event) {
                // ServiceExceptionEvent是服务异常事件，表示服务执行过程中有异常往外抛
                // 你监听这个事件后可以根据你的业务执行相应操作，比如：根据异常类型设置result等等
                
                // 关于这个事件如果是我的话，我就会根据异常类型判断是否是我自己抛出的异常，如果是的话，我就会根据异常里面的值对result设置；
                // 如果不是我自己抛出的异常（如空指针异常），则会返回给上层系统处理中，因为这个异常发生后我自己也判断不了业务执行结果到底是怎样的。
            }
        }

> 注意：服务引擎目前只有这三种类型事件，就目前来看我觉得用这三种事件来完成业务已经足够了。服务监听器是监听的所有服务的事件，而不是针对某个特定服务监听事件。

#### 3. 使用服务引擎执行服务
        
        // myService是需要执行的服务名称，XXXOrder是这个服务需要的order，XXXResult是这个服务的result（在执行过程中你肯定会对result设值）
        XXXResult result = serviceEngine.execute("myService", new XXXOrder("001"), new XXXResult());
      

## 1. 事件总线
事件总线用于在一个应用内发布事件和监听事件
> 诞生原因：当一个事件发生后，如果是由事件发布者直接调用事件监听器，那么事件发布者就必须知道有哪些事件监听器并且他们各自监听哪些事件，这样就增加了事件发布者的复杂性。从职责划分上，事件发布者并不需要关心有哪些监听器，它只负责发布事件；至于事件是怎么路由到监听器的则是事件总线的职责。

### 1. 将事件总线引入进你的系统

1. 引入事件总线依赖

        <dependency>
            <groupId>top.bekit</groupId>
            <artifactId>event</artifactId>
            <version>1.0.0.RELEASE</version>
        </dependency>

2. 如果是spring-boot项目则不需要进行任何配置。

    如果是非spring-boot项目则需要手动引入事件总线配置类EventBusConfiguration，比如：
    
        @Configuration
        @Import(EventBusConfiguration.class)
        public class MyImport {
        }

3. 在需要发布事件的地方注入事件发布器EventPublisher：

        @Autowired
        private EventPublisher eventPublisher;
    
    然后就可以发布事件：
    
        // AddUserEvent是自定义的事件类型
        eventPublisher.publish(new AddUserEvent("张三"));

### 2. 一个简单的事件总线使用样例：
一个完整的对事件总线的使用应该包括：定义事件类型、定义监听器、发布事件。
#### 1. 定义事件类型

        // 添加用户事件
        public class AddUserEvent {
            private String userName;
        
            public AddUserEvent(String userName) {
                this.userName = userName;
            }
        
            public String getUserName() {
                return userName;
            }
        }

#### 2. 定义监听器

        // 用户监听器
        @BizListener(priority = 1)  // 业务事件监听器注解（这种类型的监听器只能监听你自己发布的事件），属性priority表示优先级（具体执行顺序需要结合@Listen注解的priorityAsc属性共同决定）
        public class UserListener {
            @Listen // 监听注解
            public void listenAddUser(AddUserEvent event) {
                // 监听到事件后，执行具体业务
            }
            
            @Listen(priorityAsc = false) // priorityAsc表示是否按照优先级大小升序执行监听器，默认为升序，如果设置为false表示按照优先级大小降序执行。
            public void listenAddUser(DeleteUserEvent event) {
                // 监听到事件后，执行具体业务
            }
        }

@BizListener只是一种事件监听器类型，它监听的是使用方自己发布的事件；还会有其他类型的事件（比如框架内部的事件），这种事件需要相应类型的事件监听器才能监听到（比如“服务引擎”发出的事件就必须用@ServiceListener监听器才能监听到），具体实现是通过@Listener注解里的type属性来区别的。

一个监听器内可以监听多种类型的事件，不同监听器可以监听同一种事件。但是一个监听器内不能对同一种事件进行多次监听，原因是：1、如果一个监听器内对同一种事件进行多次监听，那么监听执行顺序怎么确定；2、基本不应该存在“一个监听器内对同一种事件进行多次监听”这种场景。

一种类型事件发生后监听器执行顺序：先执行@Listen中priorityAsc=true的监听器，并按照优先级大小升序执行；再执行@Listen中priorityAsc=false的监听器，并按照优先级大小降序执行

> 注意：本框架是为每种类型的事件监听器生成对应的事件总线（比如会为@BizListener类型的监听器统一生成一个BizListener类型的事件总线），而不是各种类型的事件监听器共用一个事件总线。@Listen注解的priorityAsc存在的原因是：可能一个监听器对某种事件需要第一个执行，但是对于另一种事件需要最后一个执行，它相当于包其他监听器涵盖在它内部。

> 监听器会被自动注册到spring容器中，因为@Listener继承了spring的@Component。

#### 3. 发布事件
上面已经将事件类型和事件监听器定义好了，现在就可以通过事件发布器发布事件了

        eventPublisher.publish(new AddUserEvent("张三"));

> 注意：通过spring获取或注入的eventPublisher是针对@BizListener这类监听器发布事件。实现方式：自动创建了一个针对@BizListener这类监听器发布事件的事件发布器EventPublisher，并把它放入了spring容器中。

## 2. 流程引擎
流程引擎比较适合订单类业务场景，功能包含流程编排和事务控制。
> 诞生原因：在订单类业务场景中，一条订单从创建到最后完成期间，往往会经历很多个状态，状态我将之称为节点，状态和状态的流转我将它称之为流程。在开发的时候，我们往往是将流程嵌入到处理过程代码中。这样做的缺点就是会将流程和处理过程耦合在一起，既不方便查看整个流程，也不方便维护处理过程。当一种新流程需要相同的处理过程时，那么这个处理过程里面就嵌入了两种流程，这样就更
不好维护了。

> 为了解决这种问题，我们势必要将流程从处理过程中剥离出来。但是流程通过什么形式来定义，以及每个节点和它对应的处理过程怎么进行关联，还有整个过程中什么时候应该新启事务，什么时候应该提交事务。为了解决这些问题，流程引擎就有了存在的价值。

### 1. 将流程引擎引入进你的系统：
1. 引入流程引擎依赖

        <dependency>
            <groupId>top.bekit</groupId>
            <artifactId>flow</artifactId>
            <version>1.0.0.RELEASE</version>
        </dependency>

2. 如果是spring-boot项目则只需要在application.properties中加入配置：

        bekit.flow.enable=true
        
    如果是非spring-boot项目则需要手动引入流程引擎配置类FlowEngineConfiguration，比如：
    
        @Configuration
        @Import(FlowEngineConfiguration.class)
        public class MyImport {
        }

3. 在需要使用的地方注入流程引擎FlowEngine：

        @Autowired
        private FlowEngine flowEngine;


    然后就可以执行流程：
    
        // demoFlow是需要执行的流程名称，trade是这个流程需要执行的目标对象（可以是订单、交易记录等等）
        trade = flowEngine.start("demoFlow", trade);
        // 注意：start方法返回的目标是最新的目标对象，和入参目标对象可能不是同一个（因为锁目标对象和插入目标对象方法执行后会更新目标对象）
        // 对执行结果进行判断（可通过trade的状态来判断），接下来就可以构造返回结果或继续进行之后的业务
    
### 2. 一个简单的流程定义样例：
一个完整的流程应该具备：流程编排（@Flow）、处理器（@Processor）、流程监听器（@FlowListener）、流程事务（@FlowTx）

#### 1. 流程编排

    流程编排的职责就是定义一个流程内所有的节点，并把这些节点之间的流转通过节点选择方法表示出来。


        @Flow  // 流程注解（流程名称默认就是类名首字母小写，也可以自己在@Flow注解里面自己指定。本流程的名称为：demoFlow）
        public class DemoFlow {
        
            @StartNode      // 开始节点，一个流程必须得有一个开始节点
            public String start() {
                // 返回下一个节点名称。节点名称默认情况下就是对应的方法名，不过也可以在节点注解上指定name属性，自己指定名称
                return "node1";
            }
        
            @ProcessNode(processor = "node1Processor")      // 处理节点（一种节点类型），属性processor指定这个节点对应的处理器
            public String node1(String processResult) {// 入参processResult是本节点对应的处理器处理后的返回结果，它的类型需要满足能被处理器返回值进行赋值就可以
                // 本方法叫做“下个节点选择方法”，意思是本节点处理器已经处理完了，现在需要选择下一个节点进行执行，返回参数就是下一个节点名称。
                // 一般下一个节点选择是根据处理器返回结果来选择。
                // 比如：处理器返回转账成功了就表示这笔业务成功了，则应该进入成功节点；如果转账失败了就表示这笔业务失败了，则应该进入失败节点；如果转账被挂起了，那么就只能停留在当前节点直到得到明确结果为止，这个时候就可以返回null，停止流程。
                // 这里为简单演示，直接返回下一个节点
                return "node2";
            }
        
            @StateNode(processor = "node2Processor")        // 状态节点
            public String node2(String processResult) {
                return "node3";
            }
        
            @WaitNode(processor = "node3Processor")         // 等待节点（下面会细说处理节点、状态节点、等待节点的区别）
            public String node3(){
                // 也可以没有processResult入参
                return "node4";
            }
        
            @ProcessNode(processor = "node4Processor")
            public String node4(String processResult) {
                return "end";
            }
        
            @EndNode        // 结束节点（一个流程必须有一个结束节点）
            public void end() {
            }
            
            @TargetMapping      // 目标对象映射
            public String targetMapping(Object target){ // 入参target是你传给流程引擎的那个目标对象
                // 将目标对象（比如订单）映射到需要执行的节点
                
                // 流程引擎并不知道当前这个目标对象应该从哪个节点开始执行（是从开始节点，还是从node1，还是从node2等等）。
                // 因为流程引擎并不知道这个目标对象是新建的，还是半路从数据库捡起来扔给流程引擎来执行的，
                // 所以需要你来告诉流程引擎现在应该从哪个节点开始执行，一般你要做的就是根据目标对象当前状态判断当前应该执行哪个节点。
                // 返回值就是需要执行的节点名称。
                // 下面的“XXX”表示逻辑判断
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
 
流程通过@Flow注解标识，流程中的节点分为开始节点（@StartNode）、处理节点（@ProcessNode）、状态节点（@StateNode）、等待节点（@WaitNode）、结束节点（@EndNode）这几种类型。
    
流程在开始前会自动的开启一个新事务并调用流程事务锁住目标对象（下面会介绍），流程正常执行结束后（无异常抛出），则会提交事务；否则如果有异常抛出，则会回滚事务（如果前面有状态节点，则已经提交的哪些事务是回滚不了的。要的就是这个效果）。
    
每个流程都必须有一个开始节点（@StartNode）和一个结束节点（@EndNode）；处理节点（@ProcessNode）执行完成后不会提交事务；状态节点处理完后会提交事务，然后会再开启一个新事务并锁住目标对象；当自动执行到需要执行等待节点时（还未执行）流程会自动停止，如果需要执行等待节点，则需要手动的调用流程引擎的start方法（这符合等待异步通知这类场景）。
 
除了开始节点和结束节点，其他类型的节点都可以通过processor属性指定一个处理器（真正干活的，就是前面说的处理过程）。
    
每个流程还必须有一个目标对象映射，它是用来让流程引擎知道应该从哪个节点开始执行。
    
> @Flow注解有个属性enableFlowTx，它是用来控制是否开启流程事务，默认是开启。如果设置为false，则流程引擎不会在事务上做任何控制（既不会主动开启事务，也不会主动提交事务，@StateNode节点效果也会跟@ProcessNode节点效果一样），当然下面要介绍的流程事务（@FlowTx）你也不需要使用了。

> 流程编排会被自动注册到spring容器中，因为@Flow继承了spring的@Component。
 
#### 2. 处理器

    处理器的职责是功能单一的执行某个节点的任务，一个处理器可以被多个节点共同使用。


        @Processor // 处理器注解（处理器名称默认就是类型首字母小写，也可以在@Processor注解里面自己指定。本处理器的名称：node1Processor）
        public class Node1Processor {
            
            @Before  // 业务前置处理，可选    
            public void before(TargetContext targetContext) { // TargetContext叫做目标上下文，可以通过他获取你传给流程引擎的目标对象（targetContext.getTarget()）
                // 最先执行
                // 可以做一些检查之类的操作
            }
        
            @Execute  // 业务处理，必选
            public String execute(TargetContext targetContext) {
                // 在@before类型方法之后执行
                // 这个是处理器的主体方法，一般就是真正干活的方法，它的返回值会成为整个处理器的返回值，然后传给流程节点中的下个节点选择方法
                return "success";
            }
        
            @After  // 业务后置处理，可选
            public void after(TargetContext targetContext) {
                // 在@Execute类型方法之后执行
                // 可以做一些结果校验之类的
            }
        
            @End    // 业务结束处理，可选
            public void end(TargetContext targetContext) {
                // 最后执行（即使发生异常也会执行）
            }
        
            @Error   // 异常处理，可选
            public void error(TargetContext targetContext) {
                // 当@before、@execute、@after类型方法任何一个发生异常后会执行
            }
        }

处理器通过@Processor进行注解，根据可能存在的需求将处理器方法分成了5种类型，只有@Execute类型方法是必须有的，其他都是可选的。
    
处理器方法可以没有入参，也可以有入参。有入参的话只能有一个入参且类型必须是TargetContext。TargetContext是目标上下文，可以通过它获取你传给流程引擎的目标对象（targetContext.getTarget()）
    
处理器执行过程中发生任何异常都会往外抛。一个处理器可以同时被多个流程使用。
    
> 处理器会被自动注册到spring容器中，因为@Processor继承了spring的@Component。

#### 3. 流程监听器

    流程监听器的职责就是监听某一个流程内发生的事件，目前只有一种类型事件————节点选择事件（这种事件一般的作用就是用来更新目标对象的状态）
    > 注意：流程监听器未使用事件总线，事件的路由是流程引擎自己完成的。这么做的原因是：为了使用上更方便。事件总线必须使用@Listen注解监听事件，但是这样的话就不能在注解里进一步明确到底是哪个节点被选择，需要你监听后自己去判断，所以流程引擎保留@ListenNodeDecide注解，让使用方更方便。


        @FlowListener(flow = "demoFlow")  // 流程监听器注解，属性flow指定被监听的流程名称
        public class DenoFlowListener {
        
            @ListenNodeDecide(nodeExpression = "node1")     // 监听节点选择事件，nodeExpression是需要被监听节点的正则表达式
            public void listenNode1(String node, TargetContext targetContext) { // node是被选择的节点名称，targetContext是目标上下文
                // 监听流程节点中的节点选择方法被执行后的返回值
                // 一般监听节点选择事件的目的是用来更新目标对象的状态，
                // 因为当节点选择事件发生时，就表明已经执行完当前节点，即将进入到下一个节点，
                // 所以目标对象的状态应该修改为下一个状态
                
                // 本监听方法只有在被选择节点是node1时才会执行
            }
        
            @ListenNodeDecide(nodeExpression = ".*")     // 监听节点选择事件，正则表达式“.*”表示监听所有的节点选择事件
            public void listenAllNode(String node, TargetContext targetContext) {
                // 本监听方法在所有节点被选择是都会执行
            }
        }

流程监听器可以监听对应流程发生的事件。目前只有节点被选择事件，此事件的作用是可以是用来更新目标对象状态，实现更新状态和流程分离；也可以干其他事，就看你需求是怎么的了。
    
监听方法的要求是要么没入参，要么入参是（String, TargetConstext）
> 流程监听器会被自动注册到spring容器中，因为@FlowListener继承了spring的@Component。

#### 4. 流程事务

    流程事务的职责就是对某一个流程事务上的操作定义，目前只有两种：锁目标对象、插入目标对象


        @FlowTx(flow = "demoFlow") // 流程事务注解，属性flow指定本流程事务所对应的流程
        public class DemoFlowTx {
        
            @LockTarget     // 锁目标对象，必选
            public Trade lockTarget(TargetContext targetContext) { // 目标上下文
                // 因为在并发情况下需要锁住目标对象下才能保证不会出错，
                // 流程引擎知道什么时候应该锁目标对象，但是流程引擎并不知道怎么锁住目标对象（不知道数据库表等信息）
                // 所以需要你在这儿指定锁目标对象的具体代码
                
                // 锁住的目标对象后必须返回被锁后的目标对象，流程引擎需要将它更新到目标上下文，
                // 因为当你锁住目标对象后，里面的内容可能已经发生变化了
            }
        
            @InsertTarget   // 插入目标对象，可选
            public Trade insertTarget(TargetContext targetContext) {
                // 插入目标对象到数据库具体代码
                // 必须返回插入后的目标对象，流程引擎需要将它更新到目标上下文
            }
        }


    流程事务必须包括锁目标对象类型方法（@LockTarget），流程在开始执行前会先开启一个新事务同时立马调用@LockTarget方法锁住目标对象，当遇到状态节点（@StateNode）执行完时会提交事务，接着会再开启新事务并再调用@LockTarget方法锁住目标对象。目的就是为了目标对象在被执行时是被锁住状态

    插入目标对象方法（@InsertTarget）的作用是开启一个新事务用来插入目标对象到数据库并提交。它存在的原因：如果在调用流程引擎前自行将目标对象插入到数据库但未提交，而流程引擎是新启事务后再锁对象，这样会导致流程引擎锁不到目标对象。所以流程引擎留一个口子专门新启一个事务让用户来插入目标对象。（需要调用流程引擎的flowEngine.insertTargetAndStart方法才会去执行插入目标对象方法）

>特别注意：如果你自己先把目标对象锁了，再调用流程引擎执行流程，那么流程引擎在开启新事务后锁目标对象时就会出现死锁，所以你在调用流程引擎前不能把目标对象锁了，这个千万要注意！！！

> 流程事务会被自动注册到spring容器中，因为@FlowTx继承了spring的@Component。

#### 5. 调用流程
上面已经定义了一个流程的所有要素，现在就可以通过流程引擎调用指定的流程

        // demoFlow是需要执行的流程名称，trade是这个流程需要执行的目标对象（可以是订单、交易记录等等）
        trade = flowEngine.start("demoFlow", trade);
        
        