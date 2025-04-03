package com.odom.travelchinese

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
import android.util.Log
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.tasks.Task
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManagerFactory
import com.odom.travelchinese.ui.theme.TravelChineseTheme
import kotlinx.coroutines.launch
import java.util.Locale

class SpeakActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                view.setBackgroundColor(getColor(R.color.white))

                // 상태 바 아이콘 색상 변경 (어두운 색상으로 설정)
                val controller = window.insetsController
                controller?.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )

                // Adjust padding to avoid overlap
                view.setPadding(0, statusBarInsets.top, 0, 0)
                insets
            }
        } else {
            // For Android 14 and below
            window.statusBarColor = getColor(R.color.black)
        }

        val Korean = intent.getStringExtra("Korean") as String
        val Chinese = intent.getStringExtra("Chinese") as String

        setContent {
            TravelChineseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    SpeakScreen(Korean, Chinese)
                }
            }
        }
    }


}

@Composable
fun SpeakScreen(korean: String, chinese: String) {
    val context = LocalContext.current
    var textToSpeech by remember { mutableStateOf<TextToSpeech?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        ListTopBar(R.drawable.ic_back)

        TextCardView(korean, chinese)

        Button(
            onClick = {
                textToSpeech = TextToSpeech(context) { status ->
                    if (status == TextToSpeech.SUCCESS) {
                        val result = textToSpeech?.setLanguage(Locale.CHINA)

                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                   //         Toast.makeText(context, "ssss", Toast.LENGTH_SHORT).show()
                        } else {
                            chinese.let { text ->
                                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        }
                    } else {
                        Toast.makeText(context, "현재는 불가합니다..", Toast.LENGTH_SHORT).show()
                    }
                }

                reviewApp(context)

            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp) // 카드 주변 여백
                .height(120.dp),
            colors = ButtonDefaults.buttonColors(Color.Black)
        ) {
            Text(
                text = " ▶ 발음",
                fontSize = 40.sp,
                color = Color.White
            )
        }

        BannerAdView(modifier = Modifier.padding(16.dp).align(Alignment.CenterHorizontally))

        InfoRow()
    }
}

@Composable
fun TextCardView(korean : String, chineseText : String) {
    Card(
        modifier = Modifier
            .padding(16.dp) // 카드 주변 여백
            .border(2.dp, Color.Black) // 테두리 두께와 색상 설정
            .fillMaxWidth(), // 카드 너비를 화면에 맞춤
        shape = MaterialTheme.shapes.large, // 카드 모서리 둥글기
        colors = CardColors(Color.White, Color.White, Color.White, Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // 카드 내부 여백
        ) {
            Text(
                text = korean,
                style = MaterialTheme.typography.bodyLarge, // 제목 스타일
                color = Color.Black // 텍스트 색상
            )
            Spacer(modifier = Modifier.height(15.dp)) // 제목과 본문 사이의 간격

            Text(
                text = chineseText,
                style = MaterialTheme.typography.displayMedium, // 본문 스타일
                color = Color.Black // 텍스트 색상
            )
        }
    }
}

@Composable
fun InfoRow() {
    val context = LocalContext.current // 현재 Context 가져오기

    Row(
        modifier = Modifier
            .padding(16.dp) // 카드 주변 여백
            .wrapContentSize(),
        verticalAlignment = Alignment.CenterVertically, // 수직 중앙 정렬
      //  horizontalArrangement = Alignment.CenterHorizontally
    ) {
        // 이미지
        Image(
            painter = painterResource(id = R.drawable.ic_info),
            contentDescription = "Info Icon",
            modifier = Modifier.size(24.dp) // 이미지 크기 조정
        )

        // CheckedTextView
        Row(
            modifier = Modifier
                .clickable {
                    requestTTSData(context) // TTS 데이터 요청 함수 호출
                }
                .padding(start = 8.dp) // 이미지와 텍스트 간의 간격
        ) {
            Text(
                text = "음성이 안 나올때",
                modifier = Modifier.weight(1f) // 텍스트가 가능한 공간을 차지하도록 설정
            )
            
        }
    }
}

// TTS 데이터 요청 함수
private fun requestTTSData(context: Context) {
    val intent = Intent(ACTION_INSTALL_TTS_DATA)
    context.startActivity(intent)
}


//  앱 리뷰
private fun reviewApp(context: Context) {
    val manager = ReviewManagerFactory.create(context)
    val request: Task<ReviewInfo> = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val reviewInfo: ReviewInfo = task.result
            manager.launchReviewFlow(context as Activity, reviewInfo)
                .addOnCompleteListener { task1: Task<Void?> ->
                    if (task1.isSuccessful) {
                        Log.d("TAG", "Review Success")
                    }
                }
        } else {
            Log.d("TAG", "Review Error")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SpeakPreview() {
    TravelChineseTheme {
        SpeakScreen("ssssss","sampleee")
    }
}