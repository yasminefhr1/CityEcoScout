package ma.ensa.projet.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CategoryFilter(title: String, iconResId: Int, count: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(110.dp)
            .padding(8.dp)
            .clickable(onClick = onClick), // Ajout de l'événement de clic
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
            Text(
                text = count,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}
