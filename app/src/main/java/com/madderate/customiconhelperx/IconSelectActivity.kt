package com.madderate.customiconhelperx

import android.content.Intent
import android.content.pm.PackageInfo
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.drawable.toBitmap
import com.madderate.customiconhelperx.base.BaseActivity
import com.madderate.customiconhelperx.ui.theme.CustomIconHelperXBasicTheme
import com.madderate.customiconhelperx.ui.views.BasicFAB
import com.madderate.customiconhelperx.ui.views.BasicTopAppBars
import com.madderate.customiconhelperx.viewmodel.IconSelectViewModel

class IconSelectActivity : BaseActivity() {
    companion object {
        @JvmStatic
        fun launch(activity: BaseActivity) {
            val intent = Intent(activity, IconSelectActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private val mViewModel by viewModels<IconSelectViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CustomIconHelperXBasicTheme { MainContent(mViewModel) }
        }
    }

    @Composable
    private fun MainContent(vm: IconSelectViewModel) {
        val response by vm.response.collectAsState()
        val packageInfos = response.result
        if (response.isLoading) {
            // loading
            return
        }
        if (!response.isLoading && !packageInfos.isNullOrEmpty()) {
            // successfully loaded
            MainContentInner(packageInfos)
            return
        }
        // load error
    }

    @Composable
    private fun MainContentInner(packageInfos: List<PackageInfo>) {
        Scaffold(
            topBar = {
                BasicTopAppBars(stringRes = R.string.icon_select_page_title) { onBackPressed() }
            },
            floatingActionButton = { BasicFAB() }
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(packageInfos) { packageInfo ->
                    PackageInfoRow(packageInfo = packageInfo)
                }
            }
        }
    }

    @Composable
    private fun PackageInfoRow(packageInfo: PackageInfo) {
        val drawable: Drawable = packageInfo.applicationInfo?.loadIcon(packageManager)
            ?: return
        val appName: String = packageInfo.applicationInfo?.loadLabel(packageManager)?.toString()
            ?: return
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(vertical = 16.dp)
        ) {
            val (icon, name) = createRefs()
            Image(
                bitmap = drawable.toBitmap().asImageBitmap(),
                contentDescription = appName,
                modifier = Modifier
                    .constrainAs(icon) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .padding(start = 16.dp)
            )
            Text(
                text = appName,
                modifier = Modifier
                    .constrainAs(name) {
                        start.linkTo(icon.end, 16.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.wrapContent
                    }
                    .padding(end = 16.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )
        }
    }

    @Preview
    @Composable
    private fun IconSelectActivityPreview() {
        CustomIconHelperXBasicTheme {
            MainContentInner(emptyList())
        }
    }
}