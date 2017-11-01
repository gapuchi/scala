Infix Operators
- a.method(b) --> a method b


Call by Name, Call by Value
    def apply[R <: { def close():Unit }, T](resource: => R)(f: R => T): T =
    - R <: ...      \\R is a subtype
    - {def close(): Unit}       \\Structural type

    Default Scala implementation of argument parameters is call by Value
    - The value of a parameter is found before it being passed.

    Another way is call by Name
    - It isn't evaluated until it is referenced. Essentially, it replaces all variables
    with the whatever is passed.


Lazy val
- Evaluated once, and then it stores it
- So such thing as lazy var since var can change
- Helps reduce overhead on expensive expressions BUT has its own overhead
    - So only use it if overhead is better than the expression


Enumerations
- Implement as an Enumeration class

    object Breed extends Enumeration {
        type Breed = Value              \\Reference as type breed instead of value
        val doberman = Value("Doberman Pinscher")
        val yorkie = Value("Yorkshire Terrier")
        val scottie = Value("Scottish Terrier")
        val dane = Value("Great Dane")
        val portie = Value("Portuguese Water Dog")
    }
    import Breed._

- the = Value(...) is a method call to assign name to each enum value. Value.toString is used to access this

Traits
- They are Java's interface except
    - also lets you define methods
    - define instance fields (different from static)
    - define type values


    class ServiceImportante(name: String) {
        def work(i: Int): Int = {
            println(s"ServiceImportante: Doing important work! $i")
            i + 1
        }
    }

    trait } Logging {
        def info(message: String): Unit
        def warning(message: String): Unit
        def error (message: String): Unit
    }

    trait } StdoutLogging extends Logging {
        def info (message: String) = println(s"INFO:$message")
        def warning(message: String) = println(s"WARNING: $message")
        def error (message: String) = println(s"ERROR:$message")
    }

    val service2 = new ServiceImportante("dos") with StdoutLogging {
        override def work(i: Int): Int = {
            info(s"Starting work: i = $i")
            val result = super.work(i)
            info(s"Ending work: i = $i, result = $result")
            result
        }
    }