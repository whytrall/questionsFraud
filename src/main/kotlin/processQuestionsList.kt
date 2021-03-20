import java.util.*

fun isPeopleNamesOrderCorrect(ppl: List<String>): Boolean {
    var prev = ppl[0]

    for (person in ppl.drop(1)) {
        if (prev == person) return false
        prev = person
    }

    return true
}

fun main() {
    val scanner = Scanner(System.`in`)
    val lines = mutableListOf<String>()
    var br = false
    val groups = mutableMapOf<Int, GroupInfo>()

    while (scanner.hasNextLine()) {
        val line = scanner.nextLine()
        if (line == "END") {
            break
        }
        else {
            lines.add(line)
        }
    }
    scanner.close()


    var currentGroup: Int? = -1
    val currentPeople = mutableListOf<Person>()
    var currentPerson: Person? = null

    for (line in lines) {
        if (line.startsWith("Group")) {
            currentGroup = line.split(' ')[1].toInt()
            //println("Parsing group $currentGroup")
            groups[currentGroup] = GroupInfo(currentGroup, mutableListOf())
        }
    }

    for (line in lines) {
        if (line.trim() == "Leftovers:") break
        if (line.trim() == "" || line.startsWith("Group")) continue

        if (line.startsWith("\t")) {
            val groupIdAndQuestion = line.split(": ")
            val groupQId = groupIdAndQuestion[0].trim().toInt()
            val question = groupIdAndQuestion[1]
            val groupForQ = groups[groupQId]!!
            val answer = groupForQ.questions.find { it.first == question }?.second!!
            currentPerson?.questions?.add(Pair(groupForQ, Pair(question, answer)))
            //println("\t\t$question ($groupQId)")
        }
        else if (currentPerson?.name != line) {
            currentPerson = Person(line)
            //println("\t${currentPerson.name}")
            (groups[currentGroup]?.groupList as MutableList).add(currentPerson)
        }
    }

    val allPeople = groups.map { it.value.groupList }.flatten()

    println("Queue: ")
    for ((_, group) in groups) {
        println("Group ${group.id}")

        val personAndQuestion = mutableListOf<Triple<String, String, String>>()

        for (person in allPeople) {
            val q = person.questions.filter { it.first == group }.map { Triple(person.name, it.second.first, it.second.second) }
            personAndQuestion.addAll(q)
        }

        while (!isPeopleNamesOrderCorrect(personAndQuestion.map { it.first })) {
            personAndQuestion.shuffle()
        }

        for (question in personAndQuestion) {
            println("\t${question.first}: ${question.second}")
            if (question.third != "-") {
                println("\t\t${question.third}")
            }
        }
    }
}