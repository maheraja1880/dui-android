import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.sample.dynamicui.ui.framework.DynamicComponent
import kotlin.random.Random
import kotlin.times

@Preview
@Composable
fun LoadingChatItemsWithShimmerPreview() {
    Column {
        LoadingChatItemNoTransition()
    }
}

//@Composable
//fun LoadingChatItemNoTransition(
//    modifier: Modifier = Modifier
//) {
//    val height = remember { listOf(36.dp, 72.dp, 108.dp).random() }
//    val startPadding = remember { Random.nextInt(16, 128).dp }
//
//    Box(
//        modifier = modifier
//            .padding(start = startPadding, top = 8.dp, end = 16.dp, bottom = 8.dp)
//            .fillMaxWidth()
//            .height(height)
//            .placeholder(
//                shape = RoundedCornerShape(18.dp),
//                visible = true,
//                highlight = PlaceholderHighlight.shimmer(),
//            )
//    )
//}
@Composable
fun LoadingChatItemNoTransition(
    modifier: Modifier = Modifier
) {
    val baseHeight = remember { listOf(36.dp, 72.dp, 108.dp).random() }
    val startPadding = remember { Random.nextInt(16, 128).dp }

    Column(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        var currentHeight = 50.dp
        repeat(3) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(currentHeight)
                    .placeholder(
                        shape = RoundedCornerShape(10.dp),
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )
            currentHeight *= 0.50f
        }
    }
}

@Composable
fun ShimmerCard () {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .background(Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, Color.LightGray),

    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            LoadingChatItemNoTransition()
        }
    }
}

@Preview
@Composable
fun PreviewShimmerCard() {
    ShimmerCard()
}

