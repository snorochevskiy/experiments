package me.snorochevskiy.myshop.util

import java.security.MessageDigest

object HashUtil:
  def md5(text: String): Array[Byte] =
    val md = MessageDigest.getInstance("MD5")
    md.update(text.getBytes)
    md.digest()

