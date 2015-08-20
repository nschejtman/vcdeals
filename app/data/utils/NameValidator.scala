package data.utils

object NameValidator {

  def validateName(name : String): String = {
    var validName = ""
    for(c : Char <- name){
      if(c != '*') validName += c
    }
    validName
  }

}
