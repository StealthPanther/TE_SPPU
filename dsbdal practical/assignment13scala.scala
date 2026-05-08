// DSBDALPrograms.scala
// Apache Spark Programs: Word Count + Bubble Sort

println("===================================")
println("PROGRAM 1: WORD COUNT")
println("===================================")

// -------------------------------
// WORD COUNT PROGRAM
// -------------------------------

// Input data
val data = Seq(
  "Spark is fast",
  "Spark is fun",
  "Scala is powerful"
)

// Convert to RDD
val rdd = sc.parallelize(data)

// Split sentences into words
val words = rdd.flatMap(line => line.split(" "))

// Convert each word into (word, 1)
val pairs = words.map(word => (word, 1))

// Count occurrences
val counts = pairs.reduceByKey((a, b) => a + b)

// Display result
println("Word Count Output:")
counts.collect().foreach(println)



println("\n===================================")
println("PROGRAM 2: BUBBLE SORT")
println("===================================")

// -------------------------------
// BUBBLE SORT PROGRAM
// -------------------------------

// Input array
val arr = Array(5, 2, 8, 1, 3)

// Convert array to RDD
var sortRDD = sc.parallelize(arr)

// Bubble Sort logic
for (i <- 0 until arr.length) {

  sortRDD = sortRDD.mapPartitions(iter => {

    val a = iter.toArray

    // Compare adjacent elements
    for (j <- 0 until a.length - 1) {

      // Swap if needed
      if (a(j) > a(j + 1)) {
        val temp = a(j)
        a(j) = a(j + 1)
        a(j + 1) = temp
      }
    }

    a.iterator
  })
}

// Display sorted result
println("Sorted Array:")
sortRDD.collect().foreach(x => print(x + " "))
println()


//nano DSBDALPrograms.scala
//spark-shell\
//:load DSBDALPrograms.scala