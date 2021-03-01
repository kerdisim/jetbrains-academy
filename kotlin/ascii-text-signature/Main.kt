import java.io.File

fun getLetterMap(pathname: String): Map<Char, Pair<List<String>, Int>> {
    val lines = File(pathname).readLines()

    val parameters = lines[0].split(" ")
    val height: Int = parameters[0].toInt()
    val characterCount: Int = parameters[1].toInt()

    val letterMap: MutableMap<Char, Pair<List<String>, Int>> = mutableMapOf()

    var currentLine = 1

    repeat(characterCount) {
        val characterInformation = lines[currentLine].split(" ")
        val letter: Char = characterInformation[0].first()
        val width: Int = characterInformation[1].toInt()

        currentLine++

        letterMap[letter] = Pair(lines.subList(currentLine, currentLine + height), width)

        currentLine += height
    }


    return letterMap.toMap()
}

fun getTextWidth(
    text: String,
    letterMap: Map<Char, Pair<List<String>, Int>>,
    spaceWidth: Int
): Int {
    var width = 0

    for (letter in text) {
        if (letter == ' ') {
            width += spaceWidth
        } else {
            val letterData = letterMap[letter]

            if (letterData != null) {
                width += letterData.second
            }
        }
    }

    return width
}

fun writeTextOnBadge(
    text: String,
    letterMap: Map<Char, Pair<List<String>, Int>>,
    badge: Array<Array<Char>>,
    spaceWidth: Int,
    topLeftRow: Int,
    topLeftCol: Int
) {
    var col = topLeftCol
    for (letter in text) {
        if (letter == ' ') {
            col += spaceWidth
            continue
        }

        val letterPair = letterMap[letter]

        if (letterPair != null) {
            val lines = letterPair.first
            val width = letterPair.second

            for (i in 0..lines.lastIndex) {
                for (j in 0 until width) {
                    badge[topLeftRow + i][col + j] = lines[i][j]
                }
            }

            col += width
        }
    }
}

fun main() {
    val romanLetters = getLetterMap("roman.txt")
    val mediumLetters = getLetterMap("medium.txt")

    // read a name from the stdin
    print("Enter name and surname: ")
    val name = readLine()!!

    // read a status from the stdin
    print("Enter person's status: ")
    val status = readLine()!!

    // calculate the width of the badge
    val romanSpaceWidth = 10
    val romanWidth = getTextWidth(name, romanLetters, romanSpaceWidth)

    val mediumSpaceWidth = 5
    val mediumWidth = getTextWidth(status, mediumLetters, mediumSpaceWidth)

    var badgeWidth = 2 + 2 + 2 + 2

    badgeWidth += if (romanWidth > mediumWidth) {
        romanWidth
    } else {
        mediumWidth
    }

    val badgeHeight = 15

    val badge: Array<Array<Char>> = Array(badgeHeight) { Array(badgeWidth) { ' ' } }

    // create badge borders

    for (col in 0..badge[0].lastIndex) {
        badge[0][col] = '8'
        badge[badge.lastIndex][col] = '8'
    }

    for (row in 0..badge.lastIndex) {
        badge[row][0] = '8'
        badge[row][1] = '8'
        badge[row][badge[0].lastIndex] = '8'
        badge[row][badge[0].lastIndex - 1] = '8'
    }

    var topLeftCol = badgeWidth / 2 - romanWidth / 2

    if (badgeWidth % 2 == 0 && romanWidth % 2 == 1) {
        topLeftCol--
    }

    writeTextOnBadge(
        name,
        romanLetters,
        badge,
        romanSpaceWidth,
        1,
        topLeftCol
    )

    topLeftCol = badgeWidth / 2 - mediumWidth / 2

    if (badgeWidth % 2 == 0 && mediumWidth % 2 == 1) {
        topLeftCol--
    }

    writeTextOnBadge(
        status,
        mediumLetters,
        badge,
        mediumSpaceWidth,
        badgeHeight - 1 - 3,
        topLeftCol
    )


    for (i in 0..badge.lastIndex) {
        for (j in 0..badge[0].lastIndex) {
            print(badge[i][j])
        }
        print('\n')
    }
}
