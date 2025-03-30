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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
            // TopBar(R.drawable.ic_menu)  todo 250330

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
                tint = Black // 아이콘 색상
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
                "탑승 수속은 몇 시에 합니까?",
                "이 비행기는 정시에 출발합니까?",
                "얼마나 지연됩니까?",
                "창문옆 좌석을 주세요.",
                "앞쪽에 앉기를 원합니다.",
                "이 좌석은 창문옆 좌석입니까?",
                "출발 시간이 언제입니까?",
                "이제 어디로 가야 합니까?",
                "몇 번 출구입니까?",
                "나의 비행기편을 확인하려고 합니다.",
                "예약을 취소하겠습니다.",
                "어느 출구로 비행기로 탑승합니까?",
                "탑승권을 좀 보여 주시겠습니까?"
            )

            val chinese1 = arrayOf(
                "什么时候办登记手续？",
                ("这座飞机正时出发吗？"),
                ( "要延迟多长时间？"),
                ("我要窗边的座位。"),
                ("我要坐在前边。"),
                "这座位是窗边吗？",
                "几点要出发？",
                "我现在得去哪里？",
                "是几号出口？",
                ("我要确认一下我的航班。"),
                "我要取消预定。",
                "我登上了飞机哪个出口？",
                ("我可以看一下您的机票吗？")
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
                "이 번호가 나의 좌석번호인데, 그 좌석이 어디 있습니까?",
                "읽을 것 좀 주시겠습니까?",
                "좌석을 좀 바꿔도 괜찮습니까?",
                "이 벨트를 어떻게 착용하는지 좀 가르쳐 주시겠습니까?",
                "맥주 한 캔을 갖다주시겠습니까?",
                "얼마를 지불해야 합니까?",
                "죄송하지만 몸이 좋지 않은데, 약 좀 주시겠습니까?",
                "담요 한장 주시겠습니까?",
                "음료수는 어떤 종류가 있습니까?"
            )

            val chinese2 = arrayOf(
                "这是我的座号，这座位在哪儿？",
                "给我拿一些报刊可以吗？",
                "我可以换位置吗？",
                "可以告诉我怎么系上安全带吗？",
                "帮我拿一瓶啤酒，好吗？",
                "我得付多少钱？",
                "不好意思，我身体有点不舒服，可以帮我拿一点药吗？",
                "可以给我拿毯子吗？",
                "都有什么饮料呢？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean2)
                putExtra("chinese" , chinese2)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_info, "btn_transfer_info") {
            // 안내소
            val korean1 = arrayOf(
                "이 도시의 지도한장 주시겠습니까?",
                "호텔 list 를 한장 주시겠습니까?",
                "이 도시에 관한 안내서 하나 주시겠습니까?",
                "버스 시간표 한장 주세요.",
                "값싼 호텔 하나를 추천해 줄 수 있습니까?",
                "이 부근에 구경할만한 관광 명소를 추천해 주시겠습니까?",
                "예약 좀 해 주시겠습니까?",
                "YMCA 호텔로 가려고 하는데 어떻게 가야하는지 좀 가르쳐주시겠습니까?",
                "약도를 좀 그려 주시겠습니까?",
                "관광버스가 있습니까?",
                "그 관광은 시간이 얼마나 소요됩니까?",
                "시간은 어느 정도 있습니까?",
                "하루종일 시간이 있습니다.",
                "시내 관광버스가 있습니까?",
                "요금은 얼마입니까?",
                "몇 시에 돌아옵니까?",
                "어디서 출발합니까?"
            )

            val chinese1 = arrayOf(
                "请给我这个城市的地图。",
                "请给我宾馆名单。",
                "请给我这个城市的介绍书。",
                "请给我公共汽车的时间表。",
                "可以给我推荐一下比较便宜的宾馆吗？",
                "可以推荐我周围的旅游景点吗？",
                "可以帮我预定吗？",
                "我要去YMCA宾馆，可以告诉我怎么去吗？",
                "可以帮我画一下简图吗？",
                "这有观光客车吗？",
                "那观光旅程需要多长时间？",
                "您有多长时间？",
                "我有一天的时间。",
                "这有市内的观光客车吗？",
                "车费是多少钱？",
                "什么时候回来？",
                "在哪里出发？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_stay, "btn_transfer_stay") {
            // 숙소
            val korean1 = arrayOf(
                "독방 하나 있습니까?",
                "1일 숙박료는 얼마입니까?",
                "너무 비싼데, 좀더 싼 방이 없습니까?",
                "이틀동안 호텔에서 묵고자하는데,어떻게 찾아가야 합니까?",
                "이것들을 세탁소에 보내주세요.",
                "6시에 꼭깨워 주기 바랍니다.",
                "체크아웃 타임이 몇 시입니까?",
                "내일 오전까지 나의 짐을 여기좀 맡겨 둬도 되겠습니까?",
                "귀중품을 좀 맡겨 두겠습니다.",
                "내일 오전에 체크아웃하려고 합니다.",
                "예약해둔것을 확인좀 해주세요.",
                "이틀더 머물겠습니다.",
                "하루 일찍 떠나겠습니다.",
                "계산서를 준비해주세요.",
                "방 하나를 예약할까요?",
                "식사도 됩니까?",
                "좋습니다. 이 작은방으로 하겠습니다."
            )

            val chinese1 = arrayOf(
                "这有一人间吗？",
                "住一天多少钱？",
                "太贵了。有没有再便宜一点的房间？",
                "我想在你们宾馆住两天，可以告诉我怎么去吗？",
                "这些衣服帮我送到洗衣房。",
                "6点必须弄醒我。",
                "几点可以结账离开？",
                "可以吧我的行李保管到明天早上吗？",
                "我要保管一下贵重品。",
                "我想在明天早上结账离开。",
                "帮我确认一下我的预定。",
                "我要再住两天。",
                "我要提前一天离开。",
                "请帮我准备一下结账书。",
                "我预定一个房间？",
                "提供饭吗？",
                "好。我决定这个小房间了。"
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
                "호텔 가는 버스가 어디 있습니까?",
                "얼마나 오래 걸립니까?",
                "호텔까지 버스 요금이 얼마입니까?",
                "중앙 우체국까지는 몇 정류장 남았습니까?",
                "택시 승차장이 어디 있습니까?",
                "호텔까지는 요금이 대략 얼마쯤 됩니까?",
                "이곳으로 좀 데려다 주시겠습니까?",
                "그건 너무 많습니다. 미터기요금대로 지불하겠습니다.",
                "정류장까지 가장 가까운 길로 갑시다.",
                "거스름돈은 그냥 가지세요.",
                "호텔 입구에서 세워주세요.",
                "몇 정류장 남았습니까?",
                "얼마나 오래 걸립니까?",

                "매표소가 어디 있습니까?",
                "좌석 하나 예매하려고 합니다.",
                "열차는 몇 시에 출발합니까?",
                "이 열차는 몇번에서 출발합니까?",
                "이 열차가 베이징행 열차입니까?",
                "기차는 여기서 얼마나 오래 정차합니까?",
                "이 열차에 식당차가 있습니까?",
                "실례하겠습니다. 이 좌석은 비었습니까?",
                "이 좌석에는 아무도 없습니까?",
                "여기가 어디입니까?",
                "차장이 어디 있습니까?",
                "어디서 열차를 갈아타야 합니까?"
            )

            val chinese1 = arrayOf(
                "在哪可以坐向宾馆的公共汽车？",
                "多久能到那儿？",
                "到宾馆得付多少公车费？",
                "到中央邮局还有几个停车站？",
                "周围哪儿有打的站？",
                "到宾馆大概得付多少钱？",
                "帮我带到这里吧。",
                "这太多了。 只给我显示器里面的价钱就可以了。",
                "让我们去最近的车站路。",
                "零钱不要了。",
                "停在宾馆路口吧。",
                "还有几个站？",
                "多久能到那儿？",

                "卖票店在哪儿？",
                "我想预定一下座位。",
                "火车几点出发？",
                "这列车在几号线出发？",
                "这这列火车是一列火车到北京？",
                "这列车要停留这多长时间。",
                "这列车有食堂间吗？",
                "不好意思，请问一下这座位是空的吗？",
                "这座没有人吗？",
                "这是哪里？",
                "售票员在哪儿？",
                "在哪可以换车？"
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
        Triple(R.string.trans_sick, "btn_transfer_sick") {
            // 아플때
            val korean1 = arrayOf(
                "이 부근에 병원이 있습니까?",
                "약국을 찾고있습니다.",
                "발목이 아픕니다.",
                "여기가 아픕니다.",
                "이빨이 아픕니다.",
                "기침이 나옵니다.",
                "얼마나 악화되었습니까?",
                "통증이 아주 심합니다.",
                "통증은 별로 없습니다.",
                "여행을 계속해도 괜찮겠습니까?",
                "이 약은 몇 시간마다 먹어야합니까?",
                "몸에 열이 있습니다.",
                "나는 거의 아무것도 먹지 못합니다.",
                "수면제를 좀 주십시요."
            )

            val chinese1 = arrayOf(
                "这周围有医院吗？",
                "我在找药店。",
                "我脚脖子疼。",
                "这里疼痛。",
                "我牙疼。",
                "我有点咳嗽。",
                "症状严重了很多吗？",
                "特别疼。",
                "不太疼。",
                "我可以继续旅行吗？",
                "这个药多久吃一次？",
                "我有点发烧。",
                "我现在什么都吃不下。",
                "给我拿点安眠药。"
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
                "지금 영업하고 있는 식당이 있습니까?",
                "두사람 앉을 수 있는 자리 좀 부탁합니다.",
                "오늘의 특별 요리는 무엇입니까?",
                "메뉴 좀 주시겠습니까?",
                "스테이크를 완전히 익혀서 주세요.",
                "계산서를 주세요.",
                "소금을 좀 집어 주십시요.",
                "계산은 내가 하겠습니다."
            )

            val chinese1 = arrayOf(
                "有现在营业的食堂吗？",
                "我要俩人坐的桌子。",
                "今天的特菜是什么？",
                "帮我拿一下菜单可以吗？",
                "我要全熟的牛排。",
                "买单。",
                "帮我拿一下盐。",
                "我来结账吧。"
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
                "그냥 구경하는 중입니다.",
                "이것은 너무 비싼 것 같군요.",
                "할인해 줄 수 있습니까?",
                "좀 더 싼 것이 있습니까?",
                "이것을 사도록 하겠습니다.",
                "모두 얼마입니까?",
                "여행자 수표를 받습니까?",
                "그것을 포장 좀 해주세요.",
                "면세품 가게가 어디 있습니까?",
                "거스름돈을 잘못 주셨습니다.",
                "카메라는 어디에 가야 살 수 있습니까?",
                "입어봐도 되겠습니까?"
            )

            val chinese1 = arrayOf(
                "我就看一看。",
                "这东西太贵了。",
                "可以打折吗？",
                "有没有再便宜一点的？",
                "我决定要买这个。",
                "这些都是多少钱？",
                "收旅行者支票吗？",
                "帮我把那个包装一下。",
                "免税店在哪里？",
                "零钱给错了。",
                "在哪可以买相机？",
                "我可以试穿吗？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_travel, "btn_transfer_travel") {
            // 관광
            val korean1 = arrayOf(
                "꼭 구경해야 할곳 몇 군데를 가르쳐주십시요.",
                "이 공원의 이름이 무엇입니까?",
                "입장료가 얼마입니까?",
                "여기서 사진 찍는 것이 허락됩니까?",
                "이 코스를 여행하는데 시간이 얼마나 걸립니까?",
                "이 코스는 식사도 나옵니까?",
                "몇 시에 돌아와야 합니까?"
            )

            val chinese1 = arrayOf(
                "告诉我必须要欣赏的景点。",
                "这公园叫什么名字？",
                "入场费是多少钱？",
                "这里允许照相吗？",
                "这旅程需费多长时间？",
                "这程序还提供饭吗？",
                "几点要回来？"
            )

            val intent = Intent(context, ListActivity::class.java).apply {
                putExtra("korean" , korean1)
                putExtra("chinese" , chinese1)
            }

            context.startActivity(intent)

        },
        Triple(R.string.trans_trouble, "btn_transfer_trouble") {
            // 문제발생
            val korean1 = arrayOf(
                "이 카메라를 어제 샀는데, 좋지 않은 것 같습니다.",
                "그것을 교환해 줄 수 있습니까?",
                "돈을 되돌려 줄 수 있습니까?",
                "영수증이 여기 있습니다.",
                "책임자를 좀 만날 수 있습니까?",
                "나는 카메라와 여권을 잃어버렸습니다.",
                "누가 나의 핸드백을 훔쳐갔습니다.",
                "죄송하지만 도와드릴 수가 없습니다.",
                "나와 아무 관계가 없습니다.",
                "유실물 취급소가 어디 있습니까?",
                "경찰서에 신고하겠습니다.",
                "잃어버린 물건을 어디서 신고합니까?"
            )

            val chinese1 = arrayOf(
                "我昨天买了这相机，但是觉得不太好。",
                "可以交换吗？",
                "可以还给我钱吗？",
                "这有收据。",
                "我可以见一下经理马？",
                "我丢了相机和护照。",
                "谁偷了我的手包。",
                "不好意思，我帮不了。",
                "这跟我无关。",
                "遗失物代办所在哪里？",
                "我要报警。",
                "在哪可以报丢失物。"
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