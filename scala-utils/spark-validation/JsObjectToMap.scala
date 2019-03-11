  def jsValueToAny(jsValue: JsValue): Any = jsValue match {
    case JsNumber(n) =>
      n match {
        case n: BigDecimal if n.isValidInt => n.intValue()
        case n: BigDecimal if n.isValidLong => n.longValue()
        case n: BigDecimal if n.isDecimalDouble => n.doubleValue()
      }
    case JsString(s) => s
    case JsTrue => true
    case JsFalse => false
    case jsObject: JsObject => jsObjectToMap(jsObject)
    case JsArray(elements) => elements.toList.map(jsValueToAny)
    case JsNull => null
  }

  def jsObjectToMap(jsObject: JsObject): Map[String, Any] = jsObject.fields.mapValues(jsValueToAny)
