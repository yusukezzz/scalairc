package net.yusukezzz.android.helloscala

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/02/02
 * Time: 14:38
 * To change this template use File | Settings | File Templates.
 */
class IrcHost(val hostname: String, val port: Int, val pass: String, val nick: String, val login: String,
              val real: String, val charset: String) {
  var conn: IrcConnection = null
  val channel: String = "#scalairc_test"

  def connection: IrcConnection = {
    if (conn == null) {
      MainActivity.localService match {
        case Some(service) => conn = service.addHost(this)
        case None => throw new Exception("Invalid service connection!")
      }
    }
    conn
  }
}
