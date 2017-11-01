Functional Programming in Scala

Anonymous Functions, Lambdas, and Closures

    var factor = 2
    val mult = (i:Int) => i * factor

// - i is a formal parameter while factor is a free variable, a reference to a variable in the enclosing scope
// The factor variable is enclosed with the mult varible and goes wherever it goes.

// Function - An operation that is named or anonymous. Its code is not evaluated until the function 
    // is called. It may or may not have free (unbound) variables in its definition.
// Lambda - An anonymous (unnamed) function. It may or may not have free (unbound) vari‐
    // ables in its definition.
// Closure - A function, anonymous or named, that closes over its environment to bind variables
    // in scope to free variables within the function.


// Method can be used as a function as long as there is no reference to this

    OBJECT.METHOD

    // Scala LIFTS the method to be a functon

Recursion

- Pure way to write loops
- annotation.tailrec used to optimize recursion

Trampoline
- recursion except function A calls B calls A calls B etcetera


Partially Applied Functions Vs Partial Functions

    def cat1(s1: String)(s2: String) = s1 + s2

    val hello = cat1("Hello ") _ //PARTIALLY APPLIED FUNCTION

    //Partial functions on the other hand are functions that are not defined for all inputs

Currying

 - Transforms a functino that takes a multiple arguments into a chain of functions each taking
   a single argument

   //Initial function
   def cat1(s1: String)(s2: String) = s1 + s2

   //Curried function
   def cat1(s1: String) = (s2: String) => s1 + s2

    - This eliminated the need for underscores when treating the curried functions as a partially applied function

   //We can convert functions that take multiple arguments into curried form.
   def cat3(s1: String, s2: String) = s1 + s2
   val cat3Curried = (cat3 _).curried ///THIS MIGHT BE OUTDATED


Tupled
- Tranforms a method with an arugment list into a function that takes one argument of a Tupled

    def mult(d1: Double, d2: Double, d3: Double) = d1 * d2 * d3
    Function.tupled(mult _)


    //NOTE: def is a method, val is a value. Passing something needs to be a FUNCTIONAL VALUE. 
    //Hence, mult is partially applied so the method is turned into a functional value.


Functional Data Structures
 - Core set: sequential collections (lists, vectors, arrays), maps, and sets
    - Each collection supports higher-order, side effect free functions called combinators
        - Ex: map, filter, fold, etcetera


    Sequences
     - collection.Seq trait is the abstraction for all mutable and immutable sequential types
        - collection.mutable.Seq and collection.immutable.Seq are child traits

        Linked list are a commonly used sequence
         - New items are prepended to the existing list
            You can create a list with the apply method

                val list1 = List("hey","buddy")

            and append with ::

                val list2 = "ahem" :: list1

            You can also create a list by appending to an empy list, FUNCTIONAL

                "ahem" :: "hey" :: "buddy" :: Nil

            Nil is the same as List.empty[Nothing]      Nothing is a subtype of all other types

            Mutable List types include ListBuffer and MutableList

        Vectors - like list but have O(1) on all ops. List have O(1) ops on its head

    Maps holds pairs of key and values

        val item = Map("key" -> "value")

        - the apply method expects a variable argument list of tuples
        - two ways to create

            val stateCapitals = Map("Alabama" -> "Montgomery","Alaska" -> "Juneau","Wyoming" -> "Cheyenne")

            val caps = stateCapitals map { case (k, v) => (k, v.toUpperCase) }

        - we can add more pairs with + 
            NOTE + will try to convert to String first, so stateCapitals + "Virginia" -> "Richmond" will
            convert the map to a string and add it to Virgina. So stateCapitals + ("Virginia" -> "Richmond")

        - Map is a trait that automatically imports the immutable. You'll have to explicitly import mutable

    Sets - unordered collections, with unique elements
     - scala.collection.Set trait defines methods for immutable ops, you need to explicitly import mutable

        + to add, ++ to add stuff from Iterators



    

    Traversal
     - foreach : the standard method, defined in scala.collection.IterableLike

        trait IterableLike[A] {
            ...
            def foreach[U](f: A => U): Unit = {...}
            ...
        }

        It returns Unit, so it is completely side effect function

    
    Mapping
     - map : returns a new collection of the same size with each element transformed by a function

        trait TraversableLike[A] { // Some details omitted.
            ...
            def map[B](f: (A) ⇒ B): Traversable[B]
            ...
        }

        actual full signature

            def map[B, That](f: A => B)(implicit bf: CanBuildFrom[Repr, B, That]): That

        - map takes a f: (A) => B, but when really it is List[A] => List[B]. It achieves this by LIFTING a function of type
        Int => String to a function of type List[Int] => List[String]


    Flat Mapping
    - flatMap : generalizing map so each element is tranformed into 0 to many elements

        def flatMap[B](f:A=> GentraverableOnce[B]): Traversable[B]

        - GentraverableOnce is an interface of anything that can be traversed at least GentraverableOnce
    - flat map is equivalent to calling map and then flatten, but it is more efficient because it doesnt generate the
    intermediate output


    Filtering

        def drop (n : Int) : TraversableLike.Repr
            Selects all elements except the first n elements. Returns a new traversable collection,
            which will be empty if this traversable collection has less than n elements.
            Traversing, Mapping, Filtering, Folding, and Reducing

        def dropWhile (p : (A) => Boolean) : TraversableLike.Repr
            Drops the longest prefix of elements that satisfy a predicate. Returns the longest
            suffix of this traversable collection whose first element does not satisfy the
            predicate p.

        def exists (p : (A) => Boolean) : Boolean
            Tests whether a predicate holds for at least one of the elements of this traversable
            collection. Returns true if so or false, otherwise.

        def filter (p : (A) => Boolean) : TraversableLike.Repr
            Selects all elements of this traversable collection that satisfy a predicate. Returns a
            new traversable collection consisting of all elements of this traversable collection
            that satisfy the given predicate p. The order of the elements is preserved.

        def filterNot (p : (A) => Boolean) : TraversableLike.Repr
            The “negation” of filter; selects all elements of this traversable collection that do
            not satisfy the predicate p...

        def find (p : (A) => Boolean) : Option[A]
            Finds the first element of the traversable collection satisfying a predicate, if any.
            Returns an Option containing the first element in the traversable collection that
            satisfies p, or None if none exists.

        def forall (p : (A) => Boolean) : Boolean
            Tests whether a predicate holds for all elements of this traversable collection. Re‐
            turns true if the given predicate p holds for all elements, or false if it doesn’t.

        def partition (p : (A) => Boolean): (TraversableLike.Repr, TraversableLike.Repr)
            Partitions this traversable collection in two traversable collections according to a
            predicate. Returns a pair of traversable collections: the first traversable collection
            consists of all elements that satisfy the predicate p and the second traversable
            collection consists of all elements that don’t. The relative order of the elements in
            the resulting traversable collections is the same as in the original traversable col‐
            lection.

        def take (n : Int) : TraversableLike.Repr
            Selects the first n elements. Returns a traversable collection consisting only of the
            first n elements of this traversable collection, or else the whole traversable collection,
            if it has less than n elements.

        def takeWhile (p : (A) => Boolean) : TraversableLike.Repr
            Takes the longest prefix of elements that satisfy a predicate. Returns the longest
            prefix of this traversable collection whose elements all satisfy the predicate p.
            Many collection types have additional methods related to filtering.


    Folding and Reducing
     - both ops shrink a collection down into a smaller collection or a single value

        Fold - starts with a seed and processes each element in the context of the seed
        Reduce - starts with the first or last element
    

    Combinators
     - The few combinators above can be combined to create more complicated.
        - The problem with OO, was that there was no concrete base, people made whatever.
        - This separates data from the behaviour    