package ma.ensa.projet.navigation





sealed class Route(val route: String) {
    object Login : Route("login")
    object Signup : Route("signup")
    object Home : Route("home")
    object Community : Route("community")
    object Favoris : Route("favoris")
    object Chat : Route("chat")
    object Profile : Route("profile")
    object AboutScreen : Route("AboutScreen")
    object Map : Route("map")
    object Search : Route("search")
}


