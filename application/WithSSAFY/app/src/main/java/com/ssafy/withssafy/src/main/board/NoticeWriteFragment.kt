package com.ssafy.withssafy.src.main.board

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ssafy.withssafy.R
import com.ssafy.withssafy.config.ApplicationClass
import com.ssafy.withssafy.config.BaseFragment
import com.ssafy.withssafy.databinding.FragmentNoticeWriteBinding
import com.ssafy.withssafy.src.dto.FcmRequest
import com.ssafy.withssafy.src.dto.Recruit
import com.ssafy.withssafy.src.dto.notice.Notice
import com.ssafy.withssafy.src.dto.notice.NoticeRequest
import com.ssafy.withssafy.src.dto.study.Study
import com.ssafy.withssafy.src.main.MainActivity
import com.ssafy.withssafy.src.network.service.FcmService
import com.ssafy.withssafy.src.network.service.NoticeService
import com.ssafy.withssafy.src.network.service.StudyService
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream

private const val TAG = "NoticeWriteFragment"
class NoticeWriteFragment : BaseFragment<FragmentNoticeWriteBinding>(FragmentNoticeWriteBinding::bind, R.layout.fragment_notice_write) {
    private var type = 0
    private var gen = ""
    private var area = ""
    private var classNum = ""
    private val NOTICE_CODE = 100
    private var classRoomId = 0
    private var fileExtension : String? = ""
    private var noticeId = 0
    private lateinit var mainActivity:MainActivity
    val userId = ApplicationClass.sharedPreferencesUtil.getUser().id
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            noticeId = it.getInt("noticeId")
        }
    }

    override fun onResume() {
        mainActivity.hideBottomNavi(true)
        super.onResume()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)

        getClassRoomListInit()
        initSpinner()
        selectSpinner()
        initButton()
        if(noticeId != 0) {
            initData()
        }
    }

    /**
     * ??????????????? ???????????? ??? ?????? ????????? ?????????
     */
    private fun initData() {
        val typeArr = arrayListOf("??????", "??????", "??????", "?????????", "??????", "??????")
        binding.fragmentNoticeWriteAppBarTitle.setText("???????????? ??????")
        binding.fragmentNoticeWriteTypeSpin.visibility = View.GONE
        binding.fragmentNoticeWriteGenSpin.visibility = View.GONE
        binding.fragmentNoticeWriteAreaSpin.visibility = View.GONE
        binding.fragmentNoticeWriteClassSpin.visibility = View.GONE
        binding.fragmentNoticeWriteTypeTxt.visibility = View.VISIBLE
        binding.fragmentNoticeWriteGenTxt.visibility = View.VISIBLE
        binding.fragmentNoticeWriteAreaTxt.visibility = View.VISIBLE
        binding.fragmentNoticeWriteClassTxt.visibility = View.VISIBLE
        noticeViewModel.notice.observe(viewLifecycleOwner) {
            binding.fragmentNoticeWriteTitleEdit.setText(it.title)
            binding.fragmentNoticeWriteContentEdit.setText(it.content)
            binding.fragmentNoticeWriteTypeTxt.setText(typeArr.get(it.typeId - 1))
            if(it.photoPath != "") {
                binding.fragmentNoticeWritePhotoGroup.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .load("${ApplicationClass.IMGS_URL}${it.photoPath}")
                        .into(binding.fragmentNoticeWritePhoto)
                }
            runBlocking {
                userViewModel.getClassRoomInfo(it.classRoomId)
            }
            userViewModel.classRoomInfo.observe(viewLifecycleOwner) {
                binding.fragmentNoticeWriteGenTxt.setText("${it.generation}???")
                binding.fragmentNoticeWriteAreaTxt.setText(it.area)
                binding.fragmentNoticeWriteClassTxt.setText(it.classDescription)
            }
        }
    }

    private fun getClassRoomListInit() {
        runBlocking {
            userViewModel.getClassRoomList()
        }
    }

    private fun initSpinner() {
        var typeList = arrayListOf("??????", "??????", "??????", "??????", "?????????", "??????", "??????")
        var genList = arrayListOf("??????")
        var areaList = arrayListOf("??????", "??????", "??????", "??????", "??????", "??????", "?????????")
        var classList = arrayListOf("??????", "??????")

        val classRoomList = userViewModel.classRommList.value
        for(i in classRoomList!!) {
            genList.add(i.generation)
            classList.add(i.classDescription)
        }
        val newGenList = genList.toSet()
        val newClassList = classList.toSet()

        val typeSpin = binding.fragmentNoticeWriteTypeSpin
        val genSpin = binding.fragmentNoticeWriteGenSpin
        val areaSpin = binding.fragmentNoticeWriteAreaSpin
        val classSpin = binding.fragmentNoticeWriteClassSpin

        typeSpin.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, typeList)
        }
        genSpin.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, newGenList.toList())
        }
        areaSpin.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, areaList)
        }
        classSpin.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, newClassList.toList())
        }
    }

    private fun initButton() {
        binding.fragmentNoticeWriteAppBarPrev.setOnClickListener{
            this@NoticeWriteFragment.findNavController().popBackStack()
        }
        binding.fragmentNoticeWriteCamera.setOnClickListener {
            mainActivity.openGallery(NOTICE_CODE)
            noticeViewModel.uploadImageUri.observe(viewLifecycleOwner) {
                if(it!=null){
                    binding.fragmentNoticeWritePhotoGroup.visibility = View.VISIBLE
                    Glide.with(requireContext())
                        .load(noticeViewModel.uploadImageUri.value)
                        .into(binding.fragmentNoticeWritePhoto)
                }
            }

        }
        binding.fragmentNoticeWritePhotoDelete.setOnClickListener {
            noticeViewModel.setUploadImageUri(null)
            //noticeViewModel.uploadImageUri = null
            binding.fragmentNoticeWritePhotoGroup.visibility = View.GONE
        }
        binding.fragmentNoticeWriteSuccess.setOnClickListener {
            if(noticeId != 0) {
                updateNotice()
            } else {
                insertNotice()
            }
        }
    }

    private fun updateNotice() {
        val title = binding.fragmentNoticeWriteTitleEdit.text.toString()
        val content = binding.fragmentNoticeWriteContentEdit.text.toString()
        if(title != "" && content != "") {
            if (noticeViewModel.uploadImageUri.value == Uri.EMPTY || noticeViewModel.uploadImageUri.value == null) {
                if(noticeViewModel.notice.value!!.photoPath != "") {
                    var notice = NoticeRequest(content, noticeViewModel.notice.value!!.photoPath, title)
                    runBlocking {
                        val response = NoticeService().updateNotice(noticeId, notice)
                        if (response.code() == 204) {
                            showCustomToast("??????????????? ?????????????????????.")
                            this@NoticeWriteFragment.findNavController().popBackStack()
                        } else {
                            showCustomToast("???????????? ????????? ??????????????????.")
                        }
                    }
                } else {
                    var notice = NoticeRequest(content, title)
                    runBlocking {
                        val response = NoticeService().updateNotice(noticeId, notice)
                        if (response.code() == 204) {
                            showCustomToast("??????????????? ?????????????????????.")
                            this@NoticeWriteFragment.findNavController().popBackStack()
                        } else {
                            showCustomToast("???????????? ????????? ??????????????????.")
                        }
                    }
                }
            } else {
                val file = File(noticeViewModel.uploadImageUri!!.value!!.path!!)
                var inputStream: InputStream? = null
                try {
                    inputStream =
                        mainActivity.contentResolver.openInputStream(noticeViewModel.uploadImageUri!!.value!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                fileExtension = mainActivity.contentResolver.getType(noticeViewModel.uploadImageUri!!.value!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
                val requestBody = RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray())
                val uploadFile = MultipartBody.Part.createFormData("file", "${file.name}.${fileExtension?.substring(6)}", requestBody)
                runBlocking {
                    val responseFile = NoticeService().insertNoticePhoto(uploadFile)
                    if (responseFile.code() == 200) {
                        if (responseFile.body() != null) {
                            Log.d(TAG, "insertNotice: ${responseFile.body()}")
                            var notice = NoticeRequest(content, responseFile.body().toString(), title)
                            Log.d(TAG, "insertNotice: $notice")
                            val response = NoticeService().updateNotice(noticeId, notice)
                            if (response.code() == 204) {
                                showCustomToast("??????????????? ?????????????????????.")
                                noticeViewModel.setUploadImageUri(null)
                                this@NoticeWriteFragment.findNavController().popBackStack()
                            } else {
                                showCustomToast("???????????? ????????? ??????????????????.")
                            }
                        }
                    }
                }
            }
        } else {
            showCustomToast("????????? ?????? ??????????????????.")
        }
    }

    private fun insertNotice() {
        val title = binding.fragmentNoticeWriteTitleEdit.text.toString()
        val content = binding.fragmentNoticeWriteContentEdit.text.toString()
        if(title != "" && content != "" && type != 0 && gen != "" && area != "" && classNum != "") {
            if (noticeViewModel.uploadImageUri.value == Uri.EMPTY || noticeViewModel.uploadImageUri.value == null) {
                var notice = Notice(classRoomId, content, title, type, userId)
                runBlocking {
                    val response = NoticeService().insertNotice(notice)
                    if (response.code() == 204) {
                        showCustomToast("??????????????? ?????????????????????.")
                        showPushFcmDialog(notice)
//                        this@NoticeWriteFragment.findNavController().popBackStack()
                    } else {
                        showCustomToast("???????????? ????????? ??????????????????.")
                    }
                }
            } else {
                val file = File(noticeViewModel.uploadImageUri!!.value!!.path!!)
                var inputStream: InputStream? = null
                try {
                    inputStream =
                        mainActivity.contentResolver.openInputStream(noticeViewModel.uploadImageUri!!.value!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                fileExtension = mainActivity.contentResolver.getType(noticeViewModel.uploadImageUri!!.value!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream)
                val requestBody = RequestBody.create(MediaType.parse("image/*"), byteArrayOutputStream.toByteArray())
                val uploadFile = MultipartBody.Part.createFormData("file", "${file.name}.${fileExtension?.substring(6)}", requestBody)
                runBlocking {
                    val responseFile = NoticeService().insertNoticePhoto(uploadFile)
                    if (responseFile.code() == 200) {
                        if (responseFile.body() != null) {
                            Log.d(TAG, "insertNotice: ${responseFile.body()}")
                            var notice = Notice(classRoomId, content, responseFile.body().toString(), title, type, userId)
                            Log.d(TAG, "insertNotice: $notice")
                            val response = NoticeService().insertNotice(notice)
                            if (response.code() == 204) {
                                showCustomToast("??????????????? ?????????????????????.")
                                showPushFcmDialog(notice)
//                                this@NoticeWriteFragment.findNavController().popBackStack()
                                noticeViewModel.setUploadImageUri(null)
                            } else {
                                showCustomToast("???????????? ????????? ??????????????????.")
                            }
                        }
                    }
                }
            }
        } else {
            showCustomToast("????????? ?????? ??????????????????.")
        }
    }

    private fun selectSpinner() {
        val typeSpin = binding.fragmentNoticeWriteTypeSpin
        val genSpin = binding.fragmentNoticeWriteGenSpin
        val areaSpin = binding.fragmentNoticeWriteAreaSpin
        val classSpin = binding.fragmentNoticeWriteClassSpin

        typeSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                type = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        genSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        gen = ""
                    }
                    else -> {
                        gen = genSpin.selectedItem.toString()

                        if(gen != "" && area != "" && classNum != "") {
                            for(i in userViewModel.classRommList.value!!) {
                                if(i.generation == gen && i.area == area && i.classDescription == classNum) {
                                    classRoomId = i.id
                                }
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        areaSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        area = ""
                    }
                    else -> {
                        area = areaSpin.selectedItem.toString()

                        // ????????? ????????? ???????????? ?????? ???????????? ????????? ?????? ???????????? ?????? ??????????????????.
                        if(gen != "" && area != "") {
                            initClassSpinner()
                        }
                        if(gen != "" && area != "" && classNum != "") {
                            for(i in userViewModel.classRommList.value!!) {
                                if(i.generation == gen && i.area == area && i.classDescription == classNum) {
                                    classRoomId = i.id
                                }
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        classSpin.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> {
                        classNum = ""
                    }
                    else -> {
                        classNum = classSpin.selectedItem.toString()

                        if(gen != "" && area != "" && classNum != "") {
                            for(i in userViewModel.classRommList.value!!) {
                                if(i.generation == gen && i.area == area && i.classDescription == classNum) {
                                    classRoomId = i.id
                                }
                            }
                        }
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }
    }

    private fun initClassSpinner() {
        var classList = arrayListOf("??????", "??????")

        val classRoomList = userViewModel.classRommList.value

        for(i in classRoomList!!) {
            if(i.generation == gen && i.area == area) {
                classList.add(i.classDescription)
            }
        }
        // ?????? ??????
        val newClassList = classList.toSet()

        val classSpin = binding.fragmentNoticeWriteClassSpin

        classSpin.apply {
            adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, newClassList.toList())
        }
    }


    private fun showPushFcmDialog(notice: Notice) {
        AlertDialog.Builder(requireContext())
            .setTitle("")
            .setMessage("?????? ?????????????????? ?????? ????????? ??????????????????????")
            .setPositiveButton("??????", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    val response : Response<Any?>
                    runBlocking {
                        response = FcmService().broadCastMsg(FcmRequest(type = 1, title = notice.title, body = notice.content))
                    }
                    this@NoticeWriteFragment.findNavController().popBackStack()
                    if(response.isSuccessful) {
                        showCustomToast("?????? ??????????????? ?????? ????????? ?????????????????????.")
                    }
                }
            })
            .setNegativeButton("??????", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    this@NoticeWriteFragment.findNavController().popBackStack()
                }
            })
            .create()
            .show()
    }

    override fun onDestroyView() {
        mainActivity.hideBottomNavi(false)
        super.onDestroyView()
    }
}