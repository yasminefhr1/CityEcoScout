package ma.ensa.projet.data

import ma.ensa.projet.models.LocalBusiness
import ma.ensa.projet.models.BusinessCategory

object SampleData {
    val localBusinesses = listOf(
        LocalBusiness(
            id = "1",
            name = "Hôtel Naturel",
            address = "67 Rue des Arbres, Lyon",
            category = BusinessCategory.HOTEL.toString(),
            latitude = 34.020882,
            longitude = -6.841650,
            imageUrl = "https://th.bing.com/th/id/R.4c22e4c201df1be432bf78759cff9ee8?rik=mdxS4hmDmQV8Yg&riu=http%3a%2f%2fimmo-centre.staticlbi.com%2foriginal%2fimages%2fbiens%2f1%2f02deeadc7e8f276c462c24d16bf9a8e4%2fd5d476837b2e732cdc1b6eef194fdae7.jpg&ehk=n9rtaMELkUTJqC8CJb6BJfPOiFrQIqwAgRRV5T6sJts%3d&risl=&pid=ImgRaw&r=0",

        )
    )
}
