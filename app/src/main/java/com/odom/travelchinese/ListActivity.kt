package com.odom.travelchinese

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.platform.LocalContext
import com.odom.travelchinese.ui.theme.TravelChineseTheme

class ListActivity : ComponentActivity() {

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

        val koreanList = intent.getStringArrayExtra("korean") as Array<String>
        val chineseList = intent.getStringArrayExtra("chinese") as Array<String>

        setContent {
            TravelChineseTheme {
                ListScreen(koreanList, chineseList)
            }
        }

    }
}

@Composable
fun ListScreen(koreanList: Array<String>, chineseList: Array<String>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        ListTopBar(R.drawable.ic_back)

        HorizontalDivider(thickness = 2.dp, color = Blue)

        // ListView를 LazyColumn으로 변환
        LazyColumn(
            modifier = Modifier.wrapContentHeight().navigationBarsPadding(),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            items(koreanList.size) { position ->
                ListItem(
                    text = koreanList[position],
                    onClick = {
                        val intent = Intent(context, SpeakActivity::class.java).apply {
                            putExtra("Korean", koreanList[position])
                            putExtra("Chinese", chineseList[position])
                        }

                        context.startActivity(intent)
                    }
                )

                HorizontalDivider(thickness = 2.dp, color = Blue)
            }

        }

    }
}

@Composable
fun ListTopBar(drawableRes : Int) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton (
            onClick = {  // 현재 Activity를 종료하여 뒤로가기
                if (context is Activity) {
                    context.finish()
                }
                      },
            modifier = Modifier.wrapContentHeight(),
            //colors = ButtonDefaults.buttonColors(Black)
        ) {
            Icon(
                painter = painterResource(id = drawableRes), // drawable 리소스
                contentDescription = "Icon Description", // 아이콘 설명
                modifier = Modifier.size(24.dp), // 아이콘 크기 조정
                tint = Black // 아이콘 색상
            )

        }

    }
}

@Composable
private fun ListItem(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(text = text)
    }
}