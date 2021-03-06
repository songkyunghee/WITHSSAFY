package com.ssafy.withssafy.src.main

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.gun0912.tedpermission.provider.TedPermissionProvider.context
import com.ssafy.withssafy.R
import com.ssafy.withssafy.config.ApplicationClass
import com.ssafy.withssafy.config.BaseActivity
import com.ssafy.withssafy.databinding.ActivityMainBinding
import com.ssafy.withssafy.src.dto.Message
import com.ssafy.withssafy.src.dto.report.Report
import com.ssafy.withssafy.src.dto.report.ReportRequest
import com.ssafy.withssafy.src.login.SingInActivity
import com.ssafy.withssafy.src.main.board.BoardDetailAdapter
import com.ssafy.withssafy.src.main.board.CommentAdapter
import com.ssafy.withssafy.src.main.home.ReportAdapter
import com.ssafy.withssafy.src.network.service.BoardService
import com.ssafy.withssafy.src.network.service.CommentService
import com.ssafy.withssafy.src.network.service.MessageService
import com.ssafy.withssafy.src.network.service.ReportService
import com.ssafy.withssafy.src.viewmodel.*
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response

private const val TAG = "MainActivity_ws"
class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate) {
    private lateinit var bottomNavigation: BottomNavigationView
    private val STORAGE = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private val STORAGE_CODE = 99
    private val NOTICE_CODE = 100
    private val BOARD_CODE = 101
    private val teamViewModel : TeamViewModel by viewModels()
    private val noticeViewModel : NoticeViewModel by viewModels()
    private val boardViewModel : BoardViewModel by viewModels()
    private val homeViewModel : HomeViewModel by viewModels()
    private val userViewModel : UserViewModel by viewModels()

    private val userId = ApplicationClass.sharedPreferencesUtil.getUser().id


    // ?????? ??????
    var permissionListener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() { // ?????? ????????? ?????? ??? ??????
//            openGallery()
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            showCustomToast("Permission Denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initNavigation()
    }

    /**
     * bottom Navi ?????????
     */
    private fun initNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainActivity_navHost) as NavHostFragment
        val navController = navHostFragment.navController
        NavigationUI.setupWithNavController(binding.mainActivityBottomNav, navController)
    }

    /**
     * BottomNavigation show/hide ??????
     * true - hide
     * false - show
     */
    fun hideBottomNavi(state : Boolean) {
        if(state) {
            binding.mainActivityBottomNav.visibility = View.GONE
        } else {
            binding.mainActivityBottomNav.visibility = View.VISIBLE
        }
    }


    /**
     * read gallery ?????? ??????
     */
    fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= 26) { // ????????? ??? ??? ?????? ??? ?????? ?????? ?????????
            val pm: PackageManager = this@MainActivity.packageManager
            if (!pm.canRequestPackageInstalls()) {
                startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:${context?.packageName}")
                    )
                )
            }
        }

        if (Build.VERSION.SDK_INT >= 23) { // ????????????(??????????????? 6.0) ?????? ?????? ??????
            TedPermission.create()
                .setPermissionListener(permissionListener)
                .setRationaleMessage("?????? ???????????? ???????????? ?????? ????????? ???????????????")
                .setDeniedMessage("If you reject permission,you can not use this service\n" +
                        "\n\nPlease turn on permissions at [Setting] > [Permission] ")
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE).check()
        }
//        else {
//            selectImg()
//        }
    }
    fun checkPermission(permissions: Array<out String>, type: Int): Boolean
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, type)
                    return false
                }
            }
        }
        return true
    }
    fun openGallery(code:Int) {
        if(checkPermission(STORAGE,STORAGE_CODE)){
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
//            filterActivityLauncher.launch(intent)
            startActivityForResult(intent,code)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                STORAGE_CODE -> {
                    teamViewModel.uploadImageUri = data?.data
//                    mainViewModels.uploadedImageUri = data?.data
//
//                    Log.d(TAG, "onActivityResult: ${data?.data}")
//                    // ????????? ??????
//                    if(mainViewModels.uploadedImageUri == null) showCustomToast("???????????? ??????????????? ?????? ?????? ???????????????.")
//                    else {
//                        mainViewModels.uploadedImage = MediaStore.Images.Media.getBitmap(contentResolver, mainViewModels.uploadedImageUri)
//                        checkTheType()
//                        photoDialog.dismiss()
//                    }
                }
                NOTICE_CODE -> {
                    noticeViewModel.setUploadImageUri(data?.data!!)
                    //noticeViewModel.uploadImageUri = data?.data
                    Log.d(TAG, "onActivityResult: NOTICE ${data?.data}")
                }
                BOARD_CODE -> {
                    boardViewModel.setBoardImgUri(data?.data!!)

                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            STORAGE_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        showCustomToast("????????? ????????? ????????? ?????????.")
                    }
                }
            }
        }
    }

    /**
     * ????????? ?????? ?????? result
     */
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { it ->
            if(it.resultCode == AppCompatActivity.RESULT_OK && it.data != null) {
                val currentImageUri = it.data?.data

                try {
                    currentImageUri?.let { uri ->
                        boardViewModel.setBoardImgUri(uri)
//                        fileExtension = requireActivity().contentResolver.getType(currentImageUri)
//                        fileExtension = fileExtension!!.substring(fileExtension!!.lastIndexOf("/") + 1, fileExtension!!.length)
                    }
                }catch(e:Exception) {
                    e.printStackTrace()
                }
            } else if(it.resultCode == AppCompatActivity.RESULT_CANCELED){
                boardViewModel.setBoardImgUri(Uri.EMPTY)
                showCustomToast("?????? ?????? ??????")
            } else{
                Log.d(TAG,"filterActivityLauncher ??????")
            }
        }

    fun logout() {
        ApplicationClass.sharedPreferencesUtil.deleteUser()
        ApplicationClass.sharedPreferencesUtil.deleteUserCookie()
        ApplicationClass.sharedPreferencesUtil.deleteAutoLogin()

        val intent = Intent(this, SingInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    fun showDialogSendMessage(toId:Int,fromId:Int) : Boolean{
        var flag = false
        var dialog = Dialog(this)
        var dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_send_message,null)
        if(dialogView.parent!=null){
            (dialogView.parent as ViewGroup).removeView(dialogView)
        }
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var params = dialog.window?.attributes
        params?.width = WindowManager.LayoutParams.MATCH_PARENT
        params?.height = WindowManager.LayoutParams.MATCH_PARENT
        dialog.window?.attributes = params
        dialog.show()
        dialogView.findViewById<ImageButton>(R.id.fragment_messageDetail_dialog_appBarPrev).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.fragment_messageDetail_dialog_insert).setOnClickListener {
            var message : Message
            if(fromId == ApplicationClass.sharedPreferencesUtil.getUser().id){
                message = Message(
                    dialogView.findViewById<EditText>(R.id.fragment_messageDetail_dialog_sendMsgContent).text.toString(),
                    0,
                    fromId,
                    toId
                )
            }else{
                message = Message(
                    dialogView.findViewById<EditText>(R.id.fragment_messageDetail_dialog_sendMsgContent).text.toString(),
                    0,
                    toId,
                    fromId
                )
            }

            runBlocking {
                val response = MessageService().insertMessage(message)
                if(response.code() == 204){
                    dialog.dismiss()
                    MessageViewModel().getMessageTalk(ApplicationClass.sharedPreferencesUtil.getUser().id, fromId)
//                    detailAdapter.notifyDataSetChanged()
                    flag = true
                }
            }
        }
        if(flag){
            return true
        }
        return false
    }

    /**
     * ????????? ?????? ???????????????
     * @param postOrComment true : ????????? ??????, false : ?????? ??????
     * @param boardTypeId -1 : userWrotePostList / -2 : userPostListOnComment / -3 : userLikePostList / -4 : hotPostList / 100 : boardListByType
     * @param detailChk true : BoardDetail?????? ????????? ?????????, false : PostDetail?????? ????????? ?????????
     */
    fun showReportDialog(id: Int, postOrComment: Boolean, reportAdapter: ReportAdapter?, commentAdapter: CommentAdapter?, boardDetailAdapter: BoardDetailAdapter?, boardTypeId: Int, detailChk: Boolean) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_report,null)

//        if(dialogView.parent != null){
//            (dialogView.parent as ViewGroup).removeAllViews()
//        }

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.show()

        dialogView.findViewById<Button>(R.id.reportDialog_btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<AppCompatButton>(R.id.reportDialog_btnReport).setOnClickListener {
            val content = dialogView.findViewById<TextView>(R.id.reportDialog_tvContent).text.toString()

            if(content.trim().isNotEmpty()) {
                val report : ReportRequest
                if(postOrComment == true) { // ????????? ????????? ??????
                    report = ReportRequest(id = 0, board = id, comment = null, content = content, user = userId)
                } else {    // ?????? ????????? ??????
                    report = ReportRequest(id = 0, board = null, comment = id, content = content, user = userId)
                }

                try {
                    var reportAllow = false // ?????? ?????? ?????? ??????

                    var dupChkResponse : Response<List<Int>>
                    runBlocking {
                        dupChkResponse = if(postOrComment == true) {
                            ReportService().getUserListByBoardId(id)
                        } else {
                            ReportService().getUserListByCommentId(id)
                        }

                        if(dupChkResponse.isSuccessful) {
                            val res = dupChkResponse.body()
                            if(res != null) {
                                if(res.isEmpty()) {
                                    reportAllow = true
                                } else {
                                    for (reporterId in res) {
                                        if(reporterId == userId) {  // ?????? ???????????? ???????????? ????????? ????????? ????????? ?????????
                                            reportAllow = false
                                            showCustomToast("?????? ?????? ????????? ????????? ????????? ????????????.")
                                            break
                                        }
                                    }
                                }
                            } else {
                                Log.e(TAG, "showReportDialog: ${dupChkResponse.message()}", )
                            }
                        } else {
                            Log.e(TAG, "showReportDialog: ${dupChkResponse.message()}", )
                        }
                    }

                    if(reportAllow) {
                        var response : Response<List<Report>>
                        runBlocking {
                            response = ReportService().addReport(report)
                        }
                        if(response.isSuccessful) {
                            val res = response.body()
                            if(res != null) {
                                if(res.size < 4) {    // ?????? ????????? 4??? ??????
                                    showCustomToast("????????? ?????????????????????.\n????????? ?????? ??? ????????? ???????????????.")
                                } else {
                                    val firstReport = res[0]
                                    if(firstReport.comment != null) { // ?????? ?????? ?????? 4??? ?????? - ?????? ?????? ??????
                                        var deleteCmtResponse : Response<Any?>

                                        runBlocking {
                                            deleteCmtResponse = CommentService().deleteComment(firstReport.comment.id)
                                        }
                                        runBlocking {
                                            homeViewModel.getReportList()
                                        }

                                        if(deleteCmtResponse.isSuccessful) {
                                            showCustomToast("????????? ?????? ????????? ???????????? ?????????????????? ?????? ????????? ?????? ?????? ???????????????.")
                                            runBlocking {
                                                boardViewModel.getCommentList(firstReport.comment.boardId)
                                            }
                                        }
                                    } else if(firstReport.board != null) {    // ????????? ?????? ?????? 4??? ?????? - ?????? ????????? ??????
                                        var deletePostResponse : Response<Any?>

                                        runBlocking {
                                            deletePostResponse = BoardService().deletePost(firstReport.board.id)
                                            homeViewModel.getReportList()
                                        }


                                        if(deletePostResponse.isSuccessful) {
                                            showCustomToast("????????? ?????? ????????? ???????????? ?????????????????? ?????? ???????????? ?????? ?????? ???????????????.")
                                            if(detailChk == true) { // boardDetail
                                                runBlocking {
                                                    boardViewModel.getUserLikePostList(userId)
                                                }
                                                when(boardTypeId) {
                                                    -2 -> {
                                                        runBlocking {
                                                            boardViewModel.getUserPostListOnComment(userId)
                                                        }
                                                    }
                                                    -4 -> {
                                                        runBlocking {
                                                            boardViewModel.getHotPostList()
                                                        }
                                                    }
                                                    else -> {
                                                        runBlocking {
                                                            boardViewModel.getBoardListByType(firstReport.board.boardType.id)
                                                        }
                                                    }
                                                }
                                                if(boardDetailAdapter != null) {
                                                    boardDetailAdapter.notifyDataSetChanged()
                                                }
                                            } else {
                                                this.onBackPressed()
                                            }
                                        }
                                    }

                                    if(reportAdapter != null) {
                                        reportAdapter.notifyDataSetChanged()
                                    }

                                    if(commentAdapter != null) {
                                        commentAdapter.notifyDataSetChanged()
                                    }
                                }
                            }
                        } else {
                            Log.e(TAG, "report: ?????? ??????", )
                        }
                    }
                    dialog.dismiss()
                } catch (e: HttpException) {
                    Log.e(TAG, "report ${e.message()}", )
                }

            } else {
                showCustomToast("?????? ????????? ????????? ?????????.")
            }
        }
    }

}
