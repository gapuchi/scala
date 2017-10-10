// Note: Matchin
// when matching cases, the  compiler assumes that lower case is a varaible and upper
// case is a type. If you want to match to a previously existing variable and not a new
// one, put the variable in back ticks.

// Note: Sequence
// The super type of all things ordered in some sense. AKA iterable

// Guarding Case
//     case _ if i%2 == 0 => ...


// Note: Deconstruction

//     case head :: tail ...

//     The list can be deconstructed since there exists a :: object with an unapply method in collections


// Case Bindings

    case class Address(street: String, city: String, country: String)
    case class Person(name: String, age: Int, address: Address)
    
    val alice = Person("Alice", 25, Address("1 Scala Lane", "Chicago", "USA"))
    val bob = Person("Bob", 29, Address("2 Java Ave.", "Miami", "USA"))
    val charlie = Person("Charlie", 32, Address("3 Python Ct.", "Boston", "USA"))
    
    for (person <- Seq(alice, bob, charlie)) {
        person match {
            case p @ Person("Alice", 25, address) => println(s"Hi Alice! $p")
            case p @ Person("Bob", 29, a @ Address(street, city, country)) =>
                println(s"Hi ${p.name}! age ${p.age}, in ${a.city}")
            case p @ Person(name, age, _) =>
                println(s"Who are you, $age year-old person named $name? $p")
        }
    }

// - The @ binds the variable prior to the object that follows


// Type Erasure
// - Scala is built on JVM and that means it has type erasure. You cant differentiate a seq of double and
//   seq of strings, since generics is erased.
// - A work around is to match on the collection and then match on the head


// Sealed Hierarchies and Exhaustive Matches

    sealed abstract class HttpMethod() {                                 // <1>
        def body: String                                                 // <2>
        def bodyLength = body.length
    }

    case class Connect(body: String) extends HttpMethod                  // <3>
    case class Delete (body: String) extends HttpMethod
    case class Get    (body: String) extends HttpMethod
    case class Head   (body: String) extends HttpMethod
    case class Options(body: String) extends HttpMethod
    case class Post   (body: String) extends HttpMethod
    case class Put    (body: String) extends HttpMethod
    case class Trace  (body: String) extends HttpMethod

    def handle (method: HttpMethod) = method match {                     // <4>
        case Connect (body) => s"connect: (length: ${method.bodyLength}) $body"
        case Delete  (body) => s"delete:  (length: ${method.bodyLength}) $body"
        case Get     (body) => s"get:     (length: ${method.bodyLength}) $body"
        case Head    (body) => s"head:    (length: ${method.bodyLength}) $body"
        case Options (body) => s"options: (length: ${method.bodyLength}) $body"
        case Post    (body) => s"post:    (length: ${method.bodyLength}) $body"
        case Put     (body) => s"put:     (length: ${method.bodyLength}) $body"
        case Trace   (body) => s"trace:   (length: ${method.bodyLength}) $body"
    }

    val methods = Seq(
    Connect("connect body..."),
    Delete ("delete body..."),
    Get    ("get body..."),
    Head   ("head body..."),
    Options("options body..."),
    Post   ("post body..."),
    Put    ("put body..."),
    Trace  ("trace body..."))

    methods foreach (method => println(handle(method)))

// - Since the class is sealed, we know it is exhaustive since no other user can add on to it and our cases
//   cover all options.     

// NOTE: An abstract no argument method declaration can be implemented by a val in a subtype
// This is because val cannot be changed, which is more restrictive than a method.
// An example of referential transparency --> Substituting a value for an expression

// It is good practice to declare val in abstract parents as an abstract, no argument method so it allows
// greater flexibility

//You can use pattern matching for extraction

    val head +: tail = List(1,2,3)

//You can use in if statements

    if (p == Person("Dean", 29, Address("1 Scala Way", "CA", "USSR"))) "yes" else "no"

    //BUT YOU CAN USE PLACEHOLDERS (_) IN IF STATEMENTS