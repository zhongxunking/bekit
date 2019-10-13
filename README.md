# bekit框架
1. 简介

> 本框架致力于解决在应用开发中公共性问题，规范开发者代码，节省开发者时间，让开发者把更多精力放在自己的业务上。目前已完成“事件总线”、“流程引擎”、“服务引擎”功能模块。

2. 环境要求
> * jdk1.8
> * Spring 4.2及以上


> 注意：本框架已经上传到[maven中央库](https://search.maven.org/#search%7Cga%7C1%7Corg.bekit)
  
3. 示例

> 结合示例看文档会更容易上手：https://github.com/zhongxunking/bekit-demo

3. 技术交流和支持
> 欢迎加我微信（zhong_xun_），进行技术交流和支持。如果本项目对你有帮助，欢迎Star和Fork。

## 1. 事件总线
事件总线用于在一个应用内发布事件和监听事件
> 诞生原因：当一个事件发生后，如果是由事件发布者直接调用事件监听器，那么事件发布者就必须知道有哪些事件监听器并且他们各自监听哪些事件，这样就增加了事件发布者的复杂性。从职责划分上，事件发布者并不需要关心有哪些监听器，它只负责发布事件；至于事件是怎么路由到监听器的则是事件总线的职责。本事件总线使用的是发布订阅模式。

### 1.1 将事件总线引入进你的系统
1. 引入事件总线依赖
```xml
<dependency>
    <groupId>org.bekit</groupId>
    <artifactId>event</artifactId>
    <version>1.2.5.RELEASE</version>
</dependency>
```

2. 如果是spring-boot项目则不需要进行任何配置。

    如果是非spring-boot项目则需要手动引入事件总线配置类EventBusConfiguration，比如：
```java
@Configuration
@Import(EventBusConfiguration.class)
public class MyImport {
}
```
3. 在需要发布事件的地方注入事件发布器EventPublisher：
```java
@Autowired
private EventPublisher eventPublisher;
```
然后就可以发布事件：
```java
// AddUserEvent是自定义的事件类型
eventPublisher.publish(new AddUserEvent("张三"));
```
### 1.2. 一个简单的事件总线使用样例
一个完整的对事件总线的使用应该包括：定义事件类型、定义监听器、发布事件。
#### 1.2.1 定义事件类型
事件类型就是普通的java类，不需要实现特定接口，也不需要打上特定注解。
```java
// 添加用户事件
public class AddUserEvent {
    // 用户id
    private String userId;
    // 用户名
    private String userName;

    public AddUserEvent(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    // 在此省略getter、setter
}
```

#### 1.2.2 定义监听器
```java
// 用户监听器
@BizListener(priority = 1)  // 业务事件监听器注解（这种类型的监听器只能监听你自己发布的事件），属性priority表示优先级（具体执行顺序需要结合@Listen注解的priorityAsc属性共同决定）
public class UserListener {
    @Listen // 监听注解（使用的是org.bekit.event.annotation.Listen）
    public void listenAddUser(AddUserEvent event) {
        // 监听到事件后，执行具体业务
    }
    
    @Listen(priorityAsc = false) // priorityAsc表示是否按照优先级大小升序执行监听器，默认为升序，如果设置为false表示按照优先级大小降序执行。
    public void listenAddUser(DeleteUserEvent event) {
        // 监听到事件后，执行具体业务
    }
}
```
@BizListener只是一种事件监听器类型，它监听的是使用方自己发布的事件；还会有其他类型的事件（比如框架内部的事件：流程引擎、服务引擎发出的事件），这种事件需要相应类型的事件监听器才能监听到（比如“服务引擎”发出的事件就必须用@ServiceListener监听器才能监听到、流程引擎需要@FlowListener）。

一个监听器内可以监听多种类型的事件，不同监听器可以监听同一种事件。但是一个监听器内不能对同一种事件进行重复监听，原因是：1、如果一个监听器内对同一种事件进行重复监听，那么监听执行顺序怎么确定？2、基本不应该存在“一个监听器内对同一种事件进行多次监听”这种场景，如果存在这种场景，你完全可以将这种监听器按照功能拆分成多个监听器。

一种类型事件发生后监听器执行顺序：先执行@Listen中priorityAsc=true的监听器，并按照优先级大小（@BizListener的priority 属性）由小到大执行；再执行@Listen中priorityAsc=false的监听器，并按照优先级大小由大到小执行；

> 注意：本框架是为每种类型的事件监听器生成对应的事件总线（比如会为@BizListener类型的监听器统一生成一个BizListener类型的事件总线），而不是各种类型的事件监听器共用一个事件总线。@Listen注解的priorityAsc存在的原因是：可能一个监听器对某种事件需要第一个执行，但是对于另一种事件却需要最后一个执行，它相当于包住其他监听器在它内部。

> 监听器会被自动注册到spring容器中，因为@Listener继承了spring的@Component。

#### 1.2.3 发布事件
上面已经将事件类型和事件监听器定义好了，现在就可以通过事件发布器发布事件了
```java
eventPublisher.publish(new AddUserEvent("123", 张三"));
```
> 注意：通过spring获取或注入的eventPublisher是针对@BizListener这类监听器发布事件。实现方式：事件总线会自动创建了一个针对@BizListener这类监听器发布事件的事件发布器EventPublisher，并把它放入了spring容器中。

#### 1.2.4 扩展
如果你只需要使用事件总线的基本功能，上面介绍的已经足够，可以忽略本部分。

有时候我们需要一些类似事件总线的功能，但具体的表现形式又和上面介绍的不太一样，难道这时候我们再来定义一套注解，再进行繁琐的解析？显然是没必要的，事件总线提供很好的扩展功能，查看org.bekit.event.extension包下的接口进行扩展。下面介绍的流程引擎中的特定流程监听器就是使用的该扩展功能，如果你需要使用扩展功能，使用时可以参考它。

## 2. 流程引擎
流程引擎比较适合订单、交易等业务场景，功能包含流程编排和事务控制。
> 注意：本流程引擎和activity这种工作流引擎完全不是一种类型的框架。本流程引擎是用于简化实时系统内部流程的定义（比如：订单、交易），流程的定义是写在代码里的；而activity是将流程定义放在activity的服务端的数据库中，从性能和安全性上显然它不适用于实时系统。

> 诞生原因：在订单类业务场景中，一条订单从创建到最后完成期间，往往会经历很多个状态，状态我将之称为节点，状态和状态的流转我将它称之为流程。在开发的时候，我们往往是将流程嵌入到处理过程代码中。这样做的缺点就是会将流程和处理过程紧密耦合在一起，既不方便查看整个流程，也不方便维护处理过程。当一种新流程需要相同的处理过程时，那么这个处理过程里面就嵌入了两种流程，复杂度呈指数级增长，这样就更不好维护了。

> 为了解决这种问题，我们势必要将流程从处理过程中剥离出来。但是流程通过什么形式来定义，以及每个节点和它对应的处理过程怎么进行关联，还有整个过程中什么时候应该新启事务，什么时候应该提交事务。为了解决这些问题，流程引擎就有了存在的价值。

### 2.1 将流程引擎引入进你的系统：
1. 引入流程引擎依赖
```xml
<dependency>
    <groupId>org.bekit</groupId>
    <artifactId>flow</artifactId>
    <version>1.2.5.RELEASE</version>
</dependency>
```
2. 如果是spring-boot项目则不需要进行任何配置。
        
    如果是非spring-boot项目则需要手动引入流程引擎配置类FlowEngineConfiguration，比如：
```java
@Configuration
@Import(FlowEngineConfiguration.class)
public class MyImport {
}
```
3. 在需要使用的地方注入流程引擎FlowEngine：
```java
@Autowired
private FlowEngine flowEngine;
```
然后就可以执行流程：
```java
Trade trade = new Trade();   // 初始化trade
// tradeFlow是需要执行的流程名称，trade是这个流程需要执行的目标对象（可以是订单、交易等等）
trade = flowEngine.start("tradeFlow", trade);
// 返回的trade是执行结束后的最新的目标对象
```

### 2.2 一个简单的流程引擎使用样例：
一个完整的流程应该具备：流程编排（@Flow）、处理器（@Processor）、特定流程监听器（@TheFlowListener）、流程事务（@FlowTx）

#### 2.2.1 流程编排
流程编排的职责就是定义一个流程内所有的节点，并把这些节点之间的流转通过节点决策器表示出来。
```java
@Flow  // 流程注解（流程名称默认就是类名首字母小写，也可以自己在@Flow注解里面自己指定。比如本流程的名称为：tradeFlow）
public class TradeFlow {

    @StartNode      // 开始节点，一个流程必须得有一个开始节点
    public String start() {
        // 返回下一个节点名称。节点名称默认情况下就是对应的方法名，不过也可以在节点注解上指定name属性，自己指定名称
        return "node1";
    }

    @ProcessNode(processor = "node1Processor")      // 处理节点（一种节点类型），属性processor指定这个节点对应的处理器
    public String node1(String processResult) {// 入参processResult是本节点对应的处理器处理后的返回结果，它的类型需要满足能被处理器返回值进行赋值就可以
        // 本方法叫做“节点决策器”，意思是本节点处理器已经执行完了，现在需要决策出下一个节点进行执行，返回参数就是下一个节点名称。
        // 一般下一个节点选择是根据处理器返回结果来选择。
        // 比如：处理器返回转账成功了就表示这笔业务成功了，则应该进入成功节点；如果转账失败了就表示这笔业务失败了，则应该进入失败节点；如果转账被挂起了，那么就只能停留在当前节点直到得到明确结果为止，这个时候就可以返回null，中断流程。
        // 这里为简单演示，直接返回下一个节点
        return "node2";
    }

    @StateNode(processor = "node2Processor")        // 状态节点
    public String node2(String processResult) {
        return "node3";
        // return null;  // 当返回null时，流程会正常中断
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

    @EndNode        // 结束节点（一个流程必须至少有一个结束节点）
    public void end() {
    }
    
    @TargetMapping      // 目标对象映射
    public String targetMapping(Trade target){ // 入参target是你传给流程引擎的那个目标对象
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
```
流程通过@Flow注解标识，流程在刚开始执行时会自动的开启一个新事务并调用流程事务锁住目标对象（下面会介绍）；当流程被正常中断或正常执行结束（无异常抛出），则会提交事务；否则如果有任何异常抛出，则会回滚事务（当然流程执行过程中已经提交的那些事务是不会回滚的，要的就是这个效果）。

流程中的节点分为开始节点（@StartNode）、状态节点（@StateNode）、处理节点（@ProcessNode）、等待节点（@WaitNode）、结束节点（@EndNode）这几种类型：
1. 开始节点（@StartNode）：每个流程都必须有一个唯一的开始节点，开始节点应该是一个流程最开始执行的节点。
2. 状态节点（@StateNode）：状态节点是一个状态开始的标志（需要新事务来执行），也是上一个状态结束的标志（需要提交老事务），所以在状态节点执行前会先提交老事务然后开启新事务并调用流程事务锁住目标对象。
3. 处理节点（@ProcessNode）：处理节点是一种单纯的处理单元，所以不会对事务进行变更（提交事务、开启新事务）。
4. 等待节点（@WaitNode）：只有当等待节点是第一个被执行的节点，等待节点才会被执行；否则流程执行到等待节点时会正常中断。（这符合等待异步通知这类场景）。
5. 结束节点（@EndNode）：每个流程必须至少有一个结束节点，它是流程结束的标志。当流程跳转到结束节点时，流程会自动结束（结束节点的方法体不会被执行）。

除了结束节点，其他类型的节点都可以通过processor属性指定一个处理器（真正干活的，就是最前面说的处理过程）。
    
每个流程还必须有一个目标对象映射方法（@TargetMapping），它是用来让流程引擎知道应该从哪个节点开始执行。
> 虽然有开始节点，但是一个流程可能是因为之前被中断了，现在是从数据库捞出来需要接着上次的节点继续执行，你就需要通过这个目标对象映射方法指定现在需要从哪个节点开始执行，通过这个方法你可以指定流程最开始的节点不是开始节点。那开始节点又有什么作用呢？开始节点在执行时和状态节点没有任何区别，它唯一的附加价值就是能让看代码的人一眼就看出一个流程最开始的节点是哪个节点。

> @Flow注解有个属性enableFlowTx，它是用来控制是否开启流程事务，默认是开启。如果设置为false，则流程引擎不会在事务上做任何控制（既不会主动开启事务，也不会主动提交事务，@StateNode节点效果也会跟@ProcessNode节点效果一样），当然下面要介绍的流程事务（@FlowTx）你也不需要使用了。

> 流程编排会被自动注册到spring容器中，因为@Flow继承了spring的@Component。

#### 2.2.2 处理器
处理器的职责是功能单一的执行某个节点的任务，一个处理器可以被多个节点共同使用。
```java
@Processor // 处理器注解（处理器名称默认就是类型名称首字母小写，也可以在@Processor注解里面自己指定。本处理器的名称：node1Processor）
public class Node1Processor {
    
    @ProcessorExecute   // 处理器方法注解
    public String execute(TargetContext<Trade> targetContext) {
        // TODO 执行具体业务
        
        return "success";  //返回值会成为处理器的返回值，然后传给流程节点中的节点决策器
    }
}
```
处理器通过@Processor进行注解，根据可能存在的需求将处理器方法分成了5种类型，只有@ProcessorExecute类型方法是必须有的，其他都是可选的。
    
处理器方法必须只能有一个入参且类型必须是TargetContext。TargetContext是目标上下文，可以通过它获取你传给流程引擎的目标对象（targetContext.getTarget()）
    
处理器执行过程中发生任何异常都会往外抛。一个处理器可以同时被多个流程使用。
> 处理器会被自动注册到spring容器中，因为@Processor继承了spring的@Component。

#### 2.2.3 特定流程监听器
特定流程监听器的职责就是监听某一个特定流程内发生的事件，目前的事件类型有：节点选择事件（这种事件一般的作用就是用来更新目标对象的状态）、流程异常事件（当一个流程执行中抛出任何异常，都会发布这个事件）
> 注意：除了特定流程监听器（@TheFlowListener），还有流程监听器（@FlowListener），区别就是流程监听器监听的是所有流程的事件。其实特定流程监听器是通过一个流程监听器（DefaultFlowListener）监听到事件后再将事件分发给特定流程监听器。

```java
@TheFlowListener(flow = "tradeFlow")  // 特定流程监听器注解，属性flow指定被监听的流程名称
public class DenoFlowListener {

    @ListenNodeDecided   // 监听节点选择事件
    public void listenNodeDecided(String node, TargetContext<Transfer> targetContext) { // 入参node表示被选择的节点，targetContext是目标上下文
        // 一般监听节点选择事件的目的是用来更新目标对象的状态，因为当节点选择事件发生时，就表明已经执行完一个节点，即将进入到下一个节点，所以目标对象的状态应该修改为下一个状态
    }

    @ListenFlowException    // 监听流程异常事件，当流程发生任何异常时都会发送这个事件
    public void listenFlowException(Throwable throwable, TargetContext<Transfer> targetContext) {
        // 本监听方法的作用就是在流程发生异常时可以做一些措施。

        // 当一个流程被某些原因中断时，我们可以通过定时任务扫描表将中间状态的交易查询出来，继续执行交易。
        // 这是个好方法，但是存在一个问题，当数据量过大时，定时任务的间隔时间不好确定，
        // 间隔时间短了就怕前面一个定时任务还没执行完第二个定时任务就开始执行了，间隔时间长了中间状态的交易就会长时间得不到执行

        // 一个更好的方式就是将发生异常的交易发送到MQ的延迟队列，同时系统监听MQ消息，当监听到这种数据时就继续执行这笔交易
        // 也就是你可以在本方法里实现将数据发送到MQ
    }
}
```
> 流程监听器会被自动注册到spring容器中，因为@FlowListener继承了spring的@Component。

#### 2.2.4 流程事务
流程事务的职责就是对某一个流程事务上的操作定义，目前只有两种：锁目标对象、插入目标对象
```java
@FlowTx(flow = "tradeFlow") // 流程事务注解，属性flow指定本流程事务所对应的流程
public class TradeFlowTx {

    @LockTarget     // 锁目标对象，必选
    public Trade lockTarget(TargetContext<Trade> targetContext) { // 目标上下文中的泛型必须和返回类型一致
        // 在并发情况下需要用锁来控制并发
        // 流程引擎知道什么时候应该锁目标对象，但是流程引擎并不知道怎么锁住目标对象（不知道数据库表等信息）
        // 所以你需要在这里实现具体锁住目标对象的代码
        
        // 锁住目标对象后必须查出目标对象最新内容并返回给流程引擎，流程引擎需要将它更新到目标上下文，
        // 因为在锁住之前目标对象可能被其他线程执行了，并修改了其中的内容
    }

    @InsertTarget   // 插入目标对象，可选
    public Trade insertTarget(TargetContext<Trade> targetContext) {
        // 在这里实现插入目标对象到数据库的具体代码
        // 必须返回插入后的目标对象，流程引擎需要将它更新到目标上下文
    }
}
```
流程事务必须包括锁目标对象类型方法（@LockTarget），流程在刚开始执行时前会先开启一个新事务同时立马调用@LockTarget方法锁住目标对象；当即将要执行状态节点（@StateNode）时，流程引擎会提交事务，再开启新事务并再调用@LockTarget方法锁住目标对象。目的就是为了目标对象在被执行时是被锁住状态

插入目标对象方法（@InsertTarget）的作用是开启一个新事务用来插入目标对象到数据库，这个方法执行完后会立马提交事务。如果这个方法抛出任何异常，都会回滚事务。它存在的原因：如果在调用流程引擎前自行将目标对象插入到数据库但未提交，而流程引擎是新启事务后再锁对象，这样会导致流程引擎锁不到目标对象。所以流程引擎留一个口子专门新启一个事务让用户来插入目标对象。（需要调用流程引擎的flowEngine.insertTargetAndStart或flowEngine.insertTarget方法才会去执行插入目标对象方法）

> 特别注意：如果你自己先把目标对象锁了，再调用流程引擎执行流程，那么流程引擎在开启新事务后锁目标对象时就会出现死锁，所以你在调用流程引擎前不能把目标对象锁了，这个千万要注意！！！

> 流程事务会被自动注册到spring容器中，因为@FlowTx继承了spring的@Component。

#### 2.2.5 调用流程
上面已经定义了一个流程的所有要素，现在就可以通过流程引擎调用指定的流程
```java
Trade trade = new Trade();   // 初始化trade
// tradeFlow是需要执行的流程名称，trade是这个流程需要执行的目标对象（可以是订单、交易等等）
trade = flowEngine.start("tradeFlow", trade);
// 返回的trade是执行结束后的最新的目标对象
```

## 3. 服务引擎
服务引擎用于规范服务执行步骤，解决服务执行中常见公共性痛点。
> 诞生原因：当一个系统需要提供服务给上层系统调用时，往往服务会进行一些固定步骤操作，比如：入参校验（像JSR303）、业务参数校验、开启事务（如果有必要）、执行业务、异常捕获、构建应答。如果每个服务都需要自己来编排这些步骤显然是没有必要的，故此服务引擎就有了存在的意义。

> 注意：服务引擎并不提供入参校验（像JSR303）、构建应答这样的能力，它提供让你监听相应事件一次解决所有服务这种问题。

### 3.1 将服务引擎引入进你的系统
1. 引入服务引擎依赖
```xml
<dependency>
    <groupId>org.bekit</groupId>
    <artifactId>service</artifactId>
    <version>1.2.5.RELEASE</version>
</dependency>
```
2. 如果是spring-boot项目则不需要进行任何配置。

    如果是非spring-boot项目则需要手动引入服务引擎配置类ServiceEngineConfiguration，比如：
```java
@Configuration
@Import(ServiceEngineConfiguration.class)
public class MyImport {
}
```
3. 在需要执行服务的地方注入服务引擎ServiceEngine：
```java
@Autowired
private ServiceEngine serviceEngine;
```
然后就可以执行服务：
```java
// myService是需要执行的服务名称，XXXOrder是这个服务需要的命令对象，result是这个服务的执行结果对象，result是由服务引擎创建的
XXXResult result = serviceEngine.execute("myService", new XXXOrder("001"));
```
### 3.2 一个简单的服务引擎使用样例
一个完整的对服务引擎的使用应该包括：定义服务、定义服务监听器、使用服务引擎执行服务。

#### 3.2.1 定义服务
```java
@Service(enableTx = true) // 服务定义注解（注意：此注解和spring的@Service注解名字一样，但是是两个不一样的注解），enableTx属性表示服务是否开启事务，默认不开启
public class MyService {
    @ServiceBefore // 服务前置处理（执行中不会有事务）
    public void check(ServiceContext<XXXOrder, XXXResult> serviceContext) { 
        // ServiceContext是服务上下文，可以通过它获取到传给服务引擎的order，也可以获取由服务引擎创建的result。
        // 进行业务校验（比如校验账户是否合法等等）
    }

    @ServiceExecute // 服务执行注解，即真正开始执行业务（注意：@Service的enableTx属性为true的话，会在进入本方法前开启新事务，如果本方法执行完后没有异常抛出，则提交事务；否则如果有任何异常抛出，则回滚事务）
    public void execute(ServiceContext<XXXOrder, XXXResult> serviceContext) {
        // 真正执行业务
    }
    
    @ServiceAfter // 服务后置处理（执行中不会有事务）
    public void serviceAfter(ServiceContext<XXXOrder, XXXResult> serviceContext) {
        // 可以进行一些后置处理，一般情况下用的不多
    }
}
```
> 被服务引擎的@Service注解的服务会被自动注册到Spring容器中（因为它继承了spring的@Component），它里面有个name属性用于指定服务名称，默认使用被注解的类名（首字母小写）。如果这个服务需要多次提交事务（比如订单类业务场景），强烈建议结合“流程引擎”一起使用，并且服务引擎不用开启事务，事务由流程引擎来控制，这样会让程序更简单（当然你完全也可以不使用流程引擎，服务引擎和流程引擎之间没有相互依赖，可以单独使用）。

#### 3.2.2 定义服务监听器
服务监听器就是专门监听服务引擎发出的事件。
```java
@ServiceListener(priority = 1)  // 服务监听器注解（专门监听服务引擎发出的事件），priority属性表示监听器优先级（具体使用方式请查看“事件总线”功能模块）
public class MyServiceListener {
    @Listen // 监听注解（具体使用方式请查看“事件总线”功能模块）
    public void listenServiceApplyEvent(ServiceApplyEvent event) {
        // ServiceApplyEvent是服务申请事件，表示即将要执行某个服务
        
        // 监听到这个事件后，可以进行一些初始化，比如：打印日志、初始化result、校验order等等
    }

    @Listen(priorityAsc = false) // priorityAsc属性表示是否优先级升序（具体使用方式请查看“事件总线”功能模块）
    public void listenServiceFinishEvent(ServiceFinishEvent event) {
        // ServiceFinishEvent是服务完成事件，表示服务已经执行完了（注意：不管服务执行中是否有异常往外抛，都会发布这个事件）
        // 你监听这个事件后可以进行一些结尾性操作，比如：打印日志。
    }

    @Listen
    public void listenServiceExceptionEvent(ServiceExceptionEvent event) {
        // ServiceExceptionEvent是服务异常事件，表示服务执行过程中有异常往外抛
        // 你监听这个事件后可以根据你的业务执行相应操作，比如：打印异常日志。
        
        // 关于这个事件如果是我的话，我就会根据异常类型判断是否是我自己定义的异常，如果是的话，我就会根据异常里面的值对result设置。
        // 如果不是我自己抛出的异常（如空指针异常），则会返回给上层系统处理中，因为这个异常发生后我自己也判断不了业务执行结果到底是怎样的。
    }
}
```
> 注意：服务引擎目前只有这三种类型事件，就目前来看我觉得用这三种事件来完成业务已经足够了。服务监听器是监听的所有服务的事件，而不是针对某个特定服务监听事件。

#### 3.2.3 使用服务引擎执行服务
```java
// myService是需要执行的服务名称，XXXOrder是这个服务需要的命令对象，result是这个服务的执行结果对象，result是由服务引擎创建的
XXXResult result = serviceEngine.execute("myService", new XXXOrder("001"));
```
