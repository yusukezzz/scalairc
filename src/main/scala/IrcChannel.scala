import java.util
import net.yusukezzz.android.helloscala.IrcUser

/**
 * Created with IntelliJ IDEA.
 * User: yusukezzz
 * Date: 2013/03/07
 * Time: 20:45
 * To change this template use File | Settings | File Templates.
 */
class IrcChannel(var ch_name: String) {
  val users = new util.ArrayList[IrcUser]
  var receive = ""

  def getUsersNum = users.size()

  def getUserNames :util.ArrayList[String] = {
    val names = new util.ArrayList[String]
    (0 to users.size()).foreach(i => names.add(users.get(i).nick))
    names
  }
}
