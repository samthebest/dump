def hbaseRowToStringList(result: Result): List[(String, String, String, Long)] = {
    for {
      columnFamilyMap <- result.getMap.entrySet().toSet.toList
      entryVersion <- columnFamilyMap.getValue().entrySet().toSet.toList
      entry <- entryVersion.getValue().entrySet().toSet.toList
    } yield {
      val row: String = Bytes.toString(result.getRow())
      val column: String = Bytes.toString(entryVersion.getKey())
      val value: String = Bytes.toString(entry.getValue())
      val ts: Long = entry.getKey()
      (row, column, value, ts)
    }
  }


def hbaseRowToStringList2(result: Result): List[String] = 
    result.list().toList.map(keyValue => 
      "Qualifier : " + keyValue.getKeyString() + " : Value : " + Bytes.toString(keyValue.getValue()))
