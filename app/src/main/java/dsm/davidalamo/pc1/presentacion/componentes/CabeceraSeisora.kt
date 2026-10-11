package dsm.davidalamo.pc1.presentacion.componentes

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dsm.davidalamo.pc1.R
import dsm.davidalamo.pc1.ui.tema.AzulMedianoche
import dsm.davidalamo.pc1.ui.tema.Blanco
import dsm.davidalamo.pc1.ui.tema.TurquesaClaro

@Composable
fun CabeceraSeisora() {
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(
                Brush.linearGradient(listOf(AzulMedianoche, androidx.compose.ui.graphics.Color(0xFF164D57))),
                RoundedCornerShape(24.dp)
            ).padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(painterResource(R.drawable.logo_seisora), "Logo de Seisora", Modifier.size(64.dp))
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("SEISORA", color = Blanco, style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold)
            Text("Seis números. Una emoción.", color = TurquesaClaro,
                style = MaterialTheme.typography.bodySmall)
        }
    }
}
