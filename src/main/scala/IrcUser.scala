package net.yusukezzz.android.helloscala

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/02/07
 * Time: 21:09
 * To change this template use File | Settings | File Templates.
 */
class IrcUser(var nick: String, var user: String, var host: String) {
  var naruto: Boolean = false

  def setNaruto(nrt: Boolean) {
    naruto = nrt
  }

  def getNaruto: Boolean = naruto
}
