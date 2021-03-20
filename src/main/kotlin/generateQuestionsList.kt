import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Math.*
import java.nio.charset.StandardCharsets
import kotlin.math.roundToInt
import kotlin.random.Random

const val CURRENT_MAX_Q = 3

fun customRound(num: Double): Int {
    val f = num.toInt()
    val s = num - f

    return if (s > 0.6) f+1 else f
}

class Person(val name: String) {
    val questions = mutableListOf<Pair<GroupInfo, Pair<String, String>>>()
}

class GroupInfo(val id: Int, val groupList: List<Person>) {
    val questions = mutableListOf<Pair<String, String>>()
    val groupActivity: Double

    init {
        val reader1 = javaClass.classLoader.getResourceAsStream("group${id}Q.txt")!!

        try {
            InputStreamReader(reader1, StandardCharsets.UTF_8).use { streamReader ->
                BufferedReader(streamReader).use { reader ->
                    var question: String? = null
                    var answer: String? = null
                    var line: String? = null
                    var i = 0
                    while (reader.readLine().also { line = it } != null) {
                        if (++i % 2 == 0) {
                            answer = line
                            if (answer != null && question != null) {
                                questions.add(Pair(question, answer))
                            }
                        }
                        else {
                            question = line
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        groupActivity = 1 - (groupList.size / questions.size.toDouble())
    }

    fun assignQuestions(vararg groups: GroupInfo) {
        val maxGroupActivity = groups.maxOf { it.groupActivity }
        val questionsPerPerson = customRound(((groupActivity / maxGroupActivity) * CURRENT_MAX_Q))

        // Вариант формулы 2
        //val allQ = groups.sumBy { it.questions.size } + questions.size
        //val maxQ = ((questions.size * (questions.size / allQ.toDouble())) + 0.3).roundToInt()

        for (person in groupList) {
            //println("Processing ${person.name}")
            var questionsAssigned = 0
            while (questionsAssigned != questionsPerPerson) {
                val howManyInitial = if (questionsPerPerson <= 2) 1 else Random.nextInt(1, questionsPerPerson)
                val howMany = if (questionsAssigned >= howManyInitial) questionsPerPerson - questionsAssigned else howManyInitial
                var randomGroup = groups.random()
                while (randomGroup.questions.size <= 1) {
                    randomGroup = groups.random()
                }
                //println("For ${person.name} will take $howMany q from group ${randomGroup.id}")
                for (i in 1..howMany) {
                    val randomQuestion = randomGroup.questions.random()
                    person.questions.add(Pair(randomGroup, randomQuestion))
                    randomGroup.questions.remove(randomQuestion)
                }

                questionsAssigned += howMany
            }
        }
    }
}

val group1List = listOf(
    Person("Ершов Михаил Николаевич"),
    Person("Сергеева Вероника Сергеевна"),
    Person("Анисин Владислав Игоревич")
)
val group2List = listOf(
    Person("Гусев Максим Игоревич"),
    Person("Денисов Александр Ильич"),
    Person("Блашенков Александр Олегович")
)
val group3List = listOf(
    Person("Белоцерковцев Богдан Михайлович"),
    Person("Баранов Никита Андреевич"),
    Person("Зенович Артем Леонидович"),
    Person("Мещерская Елизавета Александровна")
)
val group4List = listOf(
    Person("Гепалова Арина Сергеевна"),
    Person("Логвиненко Никита Константинович"),
    Person("Ельников Сергей Сергеевич"),
    Person("Мартынов Даниил Вячеславович")
)
val group5List = listOf(
    Person("Воронько Мария Дмитриевна"),
    Person("Белозеров Иван Максимович"),
    Person("Шаров Денис Денисович")
)

fun main(args: Array<String>) {
    val g1 = GroupInfo(1, group1List)
    val g2 = GroupInfo(2, group2List)
    val g3 = GroupInfo(3, group3List)
    val g4 = GroupInfo(4, group4List)
    val g5 = GroupInfo(5, group5List)

    val allGroups = listOf(g1, g2, g3, g4, g5)
    val allPeople = allGroups.map { it.groupList }.flatten()

    for (group in allGroups) {
        val otherGroups = (allGroups - group).toTypedArray()

        group.assignQuestions(*otherGroups)
    }

    for (group in allGroups) {
        println("Group ${group.id}")
        for (person in group.groupList) {
            println(person.name)
            for (question in person.questions) {
                println("\t${question.first.id}: ${question.second.first}")
            }
        }
        println()
    }

    println("Leftovers:")
    for (group in allGroups) {
        val willBeAskedQ = allPeople.map { it.questions }.flatten().filter { it.first == group }.map { it.second }
        val leftovers = group.questions - willBeAskedQ

        if (leftovers.isNotEmpty()) {
            for (question in leftovers) {
                println("\t${group.id}: ${question.first}")
            }
        }
    }

    println("END")

    /*println("------------------------\n\n\n\n\n")
    println("Order: ")
    for (group in allGroups) {
        println("Group ${group.id}")
        val personAndQuestion = mutableListOf<Triple<String, String, String>>()

        for (person in allPeople) {
            val q = person.questions.filter { it.first == group }.map { Triple(person.name, it.second.first, it.second.second) }
            personAndQuestion.addAll(q)
        }

        personAndQuestion.shuffle()

        for (question in personAndQuestion) {
            println("\t${question.first}: ${question.second}")
            if (question.third != "-") {
                println("\t\t${question.third}")
            }
        }
    }

    println("------------------------\n\n\n\n\n")

    for (group in allGroups) {
        val willBeAskedQ = allPeople.map { it.questions }.flatten().filter { it.first == group }.map { it.second }
        val leftovers = group.questions - willBeAskedQ

        println("Group ${group.id}")
        println("* will be asked: ")
        for (question in willBeAskedQ) {
            println(question.first)
            if (question.second != "-") {
                println("\t${question.second}")
            }
        }

        if (leftovers.isNotEmpty()) {
            println("* leftovers: ")
            for (question in leftovers) {
                println(question.first)
                if (question.second != "-") {
                    println("\t${question.second}")
                }
            }
        }

        println()
    }
     */
}