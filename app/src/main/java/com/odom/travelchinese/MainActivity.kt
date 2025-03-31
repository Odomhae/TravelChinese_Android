package com.odom.travelchinese

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.widget.ImageButton
import android.widget.Toast
import android.window.OnBackInvokedDispatcher
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.odom.travelchinese.ui.theme.TravelChineseTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private var mInterstitialAd: InterstitialAd? = null

    private var lastBackPressed: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) { // Android 15+
            window.decorView.setOnApplyWindowInsetsListener { view, insets ->
                val statusBarInsets = insets.getInsets(WindowInsets.Type.statusBars())
                view.setBackgroundColor(getColor(R.color.black))

                // Adjust padding to avoid overlap
                view.setPadding(0, statusBarInsets.top, 0, 0)
                insets
            }
        } else {
            // For Android 14 and below
            window.statusBarColor = getColor(R.color.black)
        }

        // 광고 초기화
        initializeAds()

        setContent {
            TravelChineseTheme {
                MainScreen()
            }
        }
    }

    override fun onBackPressed() {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastBackPressed < 2000) {
            super.onBackPressed() // 앱 종료
        } else {
            lastBackPressed = currentTime
            Toast.makeText(this, "뒤로 가기를 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initializeAds() {
        // AdMob 초기화
        MobileAds.initialize(this) {
            Log.d("test", "Ad loaded")
        }

        // 전면 광고 로드
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            getString(R.string.TEST_fullscreen_ad_unit_id),
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.show(this@MainActivity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            }
        )
    }
}

@Composable
fun MainScreen() {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(White),
        color = Color.White
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
             TopBar(R.drawable.ic_menu)

            // Main Content
            ScrollableContent()
        }
    }
}

@Composable
private fun TopBar(drawableRes : Int) {
        Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        IconButton (
            onClick = { },
            modifier = Modifier.wrapContentHeight(),
        ) {
            Icon(
                painter = painterResource(drawableRes), // drawable 리소스
                contentDescription = "Icon Description", // 아이콘 설명
                modifier = Modifier.size(24.dp), // 아이콘 크기 조정
                tint = Transparent// Black // 아이콘 색상 todo 250331
            )

        }

    }
}

@Composable
private fun ScrollableContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Grid of Buttons
        ButtonGrid()

        BannerAdView(
            modifier = Modifier
                .align(Alignment.CenterHorizontally) // Box의 하단 중앙에 배치
                .padding(16.dp) // 여백 추가
        )
    }
}

@Composable
private fun ButtonGrid() {
    val context = LocalContext.current

    val buttonModifier = Modifier//.wrapContentWidth(Alignment.CenterHorizontally)
        .size(100.dp)
        .border(4.dp, Blue, shape = MaterialTheme.shapes.large) // 테두리 두께와 색상 설정

    val buttons = listOf(
        Triple(R.string.trans_airport, "btn_transfer_choolkuk") {
            // 공항
            val korean1 = arrayOf(
                "체크인 카운터가 어디에 있나요?", "저는 비행기를 놓쳤어요, 어떻게 해야 하나요?", "수하물 찾는 곳은 어디인가요?", "출입국 심사 구역은 어떻게 가나요?", "분실물 보관소가 있나요?",
                "제 비행기를 변경할 수 있나요?", "택시는 어디서 탈 수 있나요?", "여기서 Wi-Fi를 사용할 수 있나요?", "도심까지 가는 데 얼마나 걸리나요?"
            )

            val chinese1 = arrayOf(
                "办理登机手续的柜台在哪里？", "我错过了航班，我应该怎么办？", "行李领取处在哪里？", "我怎么去移民检查区？", "有失物招领处吗？",
                "我可以更改我的航班吗？", "我在哪里可以找到出租车？", "这里有Wi-Fi吗？", "到市中心需要多长时间？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },

        Triple(R.string.trans_air, "btn_transfer_air") {
            //  기내
            val korean2 = arrayOf(
                "물 좀 주시겠어요?", "우리는 언제 도착하나요?", "담요를 주실 수 있나요?", "이 비행기에는 식사가 제공되나요?", "화장실이 어디인가요?",
                "자리를 바꿀 수 있나요?", "창문 블라인드를 내릴 수 있나요?", "비행 시간은 얼마나 되나요?", "기내 오락 시설이 있나요?", "비행 중에 휴대폰을 사용할 수 있나요?"
            )

            val chinese2 = arrayOf(
                "请给我一些水好吗？", "我们什么时候到达？", "请给我一条毯子好吗？", "这班航班有餐食吗？", "洗手间在哪里？",
                "我可以换座位吗？", "请拉下窗帘好吗？", "飞行时间是多久？", "飞机上有娱乐设施吗？", "我可以在飞行中使用手机吗？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean2)
                putExtra("chinese" , chinese2)
            }

            context.startActivity(intent)

        },
//        Triple(R.string.trans_info, "btn_transfer_info") {
//            // 안내소
//            val korean1 = arrayOf(
//                "이 도시의 지도한장 주시겠습니까?",
//                "호텔 list 를 한장 주시겠습니까?",
//                "이 도시에 관한 안내서 하나 주시겠습니까?",
//                "버스 시간표 한장 주세요.",
//                "값싼 호텔 하나를 추천해 줄 수 있습니까?",
//                "이 부근에 구경할만한 관광 명소를 추천해 주시겠습니까?",
//                "예약 좀 해 주시겠습니까?",
//                "YMCA 호텔로 가려고 하는데 어떻게 가야하는지 좀 가르쳐주시겠습니까?",
//                "약도를 좀 그려 주시겠습니까?",
//                "관광버스가 있습니까?",
//                "그 관광은 시간이 얼마나 소요됩니까?",
//                "시간은 어느 정도 있습니까?",
//                "하루종일 시간이 있습니다.",
//                "시내 관광버스가 있습니까?",
//                "요금은 얼마입니까?",
//                "몇 시에 돌아옵니까?",
//                "어디서 출발합니까?"
//            )
//
//            val chinese1 = arrayOf(
//                "请给我这个城市的地图。",
//                "请给我宾馆名单。",
//                "请给我这个城市的介绍书。",
//                "请给我公共汽车的时间表。",
//                "可以给我推荐一下比较便宜的宾馆吗？",
//                "可以推荐我周围的旅游景点吗？",
//                "可以帮我预定吗？",
//                "我要去YMCA宾馆，可以告诉我怎么去吗？",
//                "可以帮我画一下简图吗？",
//                "这有观光客车吗？",
//                "那观光旅程需要多长时间？",
//                "您有多长时间？",
//                "我有一天的时间。",
//                "这有市内的观光客车吗？",
//                "车费是多少钱？",
//                "什么时候回来？",
//                "在哪里出发？"
//            )
//
//            val intent = Intent(context, ListActivity::class.java).apply {
//                putExtra("korean" , korean1)
//                putExtra("chinese" , chinese1)
//            }
//
//            context.startActivity(intent)
//
//        },
        Triple(R.string.trans_stay, "btn_transfer_stay") {
            // 숙소
            val korean1 = arrayOf(
                "객실 요금에 아침식사가 포함되어 있나요?", "모닝콜을 예약할 수 있나요?", "엘리베이터가 어디에 있나요?", "수영장이 있나요?",
                "수건을 추가로 주세요.", "전망이 좋은 방을 받을 수 있나요?", "체크아웃 시간은 언제인가요?", "Wi-Fi에 어떻게 연결하나요?", "방에 금고가 있나요?"
            )

            val chinese1 = arrayOf(
                "房费包括早餐吗？", "我可以预约叫醒服务吗？", "电梯在哪里？", "有游泳池吗？",
                "请给我额外的毛巾。", "我可以要一间有景观的房间吗？", "退房时间是什么时候？", "如何连接Wi-Fi？", "房间里有保险箱吗？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_restaurant, "btn_transfer_restaurant") {
            // 식당
            val korean1 = arrayOf(
                "한국어 메뉴판이 있나요?", "저는 견과류 알레르기가 있어요. 이 요리에 견과류가 들어 있나요?", "계산서를 부탁드립니다.", "흡연 구역이 아닌 곳은 어디인가요?",
                "지역 특선 요리를 추천해 주세요.", "스테이크는 잘 익힌 것으로 주세요.", "두 명용 테이블을 주세요.", "비건 메뉴가 있나요?", "이걸 포장할 수 있나요?",
                "덜 맵게 요리해주세요.", "음식이 너무 짭니다."
            )

            val chinese1 = arrayOf(
                "有韩语菜单吗？", "我对坚果过敏，这道菜里有坚果吗？", "请给我账单。", "有禁烟区吗？",
                "你能推荐一道本地特色菜吗？", "我想要我的牛排全熟。", "我可以要一张两人桌吗？", "你们有素食菜单吗？", "我可以打包带走吗？",
                "请做得不太辣。", "菜太咸了。"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_sick, "btn_transfer_sick") {
            // 아플때
            val korean1 = arrayOf(
                "의사를 만나야 해요.", "응급실이 어디인가요?", "치료 비용이 얼마인가요?", "처방전이 필요해요.", "여기가 아픕니다.",
                "근처에 약국이 있나요?", "진통제가 있나요?", "물 좀 주시겠어요?", "머리가 어지러워요.", "영어를 할 수 있는 의사가 있나요?"
            )

            val chinese1 = arrayOf(
                "我需要看医生。", "急诊室在哪里？", "这治疗需要多少钱？", "我需要一张处方。", "这里很痛。",
                "附近有药店吗？", "你们有止痛药吗？", "请给我一些水好吗？", "我感到头晕。", "有会说英语的医生吗？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_transpotation, "btn_transfer_transpotation") {
            // 대중교통
            val korean1 = arrayOf(
                "티켓은 어디서 살 수 있나요?", "공항까지 어떻게 가나요?", "가장 가까운 지하철역이 어디인가요?",
                "마지막 기차는 언제 출발하나요?", "여기서 택시를 탈 수 있나요?", "이 버스는 24시간 운행하나요?", "시내까지 얼마나 걸리나요?"
            )

            val chinese1 = arrayOf(
                "我在哪里可以买票？", "我怎么去机场？", "最近的地铁站在哪里？",
                "最后一班火车什么时候离开？", "我可以在这里打到出租车吗？", "这班公交车是24小时运行的吗？", "到市中心有多远？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_buy, "btn_transfer_buy") {
            // 물건구입
            val korean1 = arrayOf(
                "신용카드로 결제할 수 있나요?", "현금을 받나요?", "할부로 결제할 수 있나요?", "서비스 요금이 있나요?", "학생 할인 있나요?",
                "영수증을 받을 수 있나요?", "계산서를 나누어서 낼 수 있나요?", "팁이 계산서에 포함되었나요?", "모바일 결제할 수 있나요?", "환불을 받을 수 있나요?"
            )

            val chinese1 = arrayOf(
                "我可以用信用卡支付吗？", "你们接受现金吗？", "我可以分期付款吗？", "有服务费吗？", "你们有学生优惠吗？",
                "我可以要收据吗？", "我想分开付款。", "小费包括在账单中吗？", "我可以用手机支付吗？", "我可以退款吗？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_ask, "btn_transfer_ask") {
            // 길 물을때
            val korean1 = arrayOf(
                "죄송하지만, 호텔가는길 좀 가르쳐주시겠습니까?",
                "가장 가까운 지하철역이 어디 있습니까?",
                "공중전화가 어디 있습니까?",
                "약도를 좀 그려주시겠습니까?",
                "저도 여기는 처음이라 잘 모르겠습니다.",
                "죄송하지만 시내 중심가로 가려면 어떻게 갑니까?",
                "죄송하지만 이 부근에 백화점이 있습니까?"
            )

            val chinese1 = arrayOf (
                "不好意思，请问一下到宾馆怎么走？",
                "离这最近的地铁站在哪里？",
                "公用电话在哪儿？",
                "可以帮我画一下简图吗？",
                "我也是第一次来这里，我也不熟悉。",
                "不好意思，要去市内怎么去呀？",
                "不好意思，问一下这周围有没有百货店?"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },


//        Triple(R.string.trans_travel, "btn_transfer_travel") {
//            // 관광
//            val korean1 = arrayOf(
//                "꼭 구경해야 할곳 몇 군데를 가르쳐주십시요.",
//                "이 공원의 이름이 무엇입니까?",
//                "입장료가 얼마입니까?",
//                "여기서 사진 찍는 것이 허락됩니까?",
//                "이 코스를 여행하는데 시간이 얼마나 걸립니까?",
//                "이 코스는 식사도 나옵니까?",
//                "몇 시에 돌아와야 합니까?"
//            )
//
//            val chinese1 = arrayOf(
//                "告诉我必须要欣赏的景点。",
//                "这公园叫什么名字？",
//                "入场费是多少钱？",
//                "这里允许照相吗？",
//                "这旅程需费多长时间？",
//                "这程序还提供饭吗？",
//                "几点要回来？"
//            )
//
//            val intent = Intent(context, ListActivity::class.java).apply {
//                putExtra("korean" , korean1)
//                putExtra("chinese" , chinese1)
//            }
//
//            context.startActivity(intent)
//
//        },
        Triple(R.string.trans_trouble, "btn_transfer_trouble") {
            // 문제발생
            val korean1 = arrayOf(
                "도와주실 수 있나요? 여권을 잃어버렸어요.", "이해하지 못했어요. 다시 말씀해 주세요.", "영어 할 수 있나요?", "도움이 필요해요.", "가장 가까운 경찰서가 어디인가요?",
                "제 전화가 고장 났어요.", "제 전화 충전기 있나요?", "지갑을 잃어버렸어요.", "택시를 불러주실 수 있나요?", "근처에 호텔이 있나요?", "저는 한국인입니다."
            )

            val chinese1 = arrayOf(
                "你能帮我吗？我丢了护照。", "我不明白。你能再说一遍吗？", "你会说英语吗？", "我需要帮助。", "最近的警察局在哪里？",
                "我的手机坏了。", "有没有适合我手机的充电器？", "我丢了钱包。", "你能为我叫辆出租车吗？", "附近有酒店吗？", "我是韩国人。"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        }
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        // Create 4 rows with 3 buttons each
        for (i in 0..3) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (j in 0..2) {
                    val index = i * 3 + j
                    if (index < buttons.size) {
                        val (textRes, _, onClick) = buttons[index]
                        Button(
                            onClick = onClick,
                            modifier = buttonModifier,
                            colors = ButtonDefaults.buttonColors(White),
                            shape = MaterialTheme.shapes.large
                        ) {
                            Text(
                                text = stringResource(textRes),
                                color = Black,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = context.getString(R.string.TEST_banner_ad_unit_id)
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier = modifier.wrapContentSize()//Modifier.fillMaxWidth()
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TravelChineseTheme {
        MainScreen()
    }
}