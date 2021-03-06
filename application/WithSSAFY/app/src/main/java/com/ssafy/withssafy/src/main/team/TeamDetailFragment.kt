package com.ssafy.withssafy.src.main.team

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.withssafy.R
import com.ssafy.withssafy.config.ApplicationClass
import com.ssafy.withssafy.config.BaseFragment
import com.ssafy.withssafy.databinding.FragmentTeamDetailBinding
import com.ssafy.withssafy.src.dto.Message
import com.ssafy.withssafy.src.dto.study.StudyMember
import com.ssafy.withssafy.src.dto.study.StudyMemberRequest
import com.ssafy.withssafy.src.main.MainActivity
import com.ssafy.withssafy.src.main.board.CommentAdapter
import com.ssafy.withssafy.src.network.service.MessageService
import com.ssafy.withssafy.src.network.service.StudyService
import com.ssafy.withssafy.src.viewmodel.TeamViewModel
import kotlinx.coroutines.runBlocking

private const val TAG = "TeamDetailFragment"
class TeamDetailFragment : BaseFragment<FragmentTeamDetailBinding>(FragmentTeamDetailBinding::bind, R.layout.fragment_team_detail) {
    lateinit var mainActivity: MainActivity

    private var studyId = 0
    private lateinit var studyCommentAdapter: CommentAdapter
    private val userId = ApplicationClass.sharedPreferencesUtil.getUser().id

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onResume() {
        super.onResume()
        mainActivity.hideBottomNavi(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            studyId = it.getInt("studyId")
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity.hideBottomNavi(true)

        binding.viewModel = teamViewModel
        runBlocking {
            teamViewModel.getStudy(studyId)
            teamViewModel.getStudyCommentByBoardId(studyId)
        }
        setListener()
    }
    private fun setListener(){
        initData()
        initButtons()
        initAdapter()
        commentLayoutClickEvent()
    }
    private fun initData(){
        if(teamViewModel.study.value!!.photoPath == null || teamViewModel.study.value!!.photoPath == ""){
            binding.fragmentTeamDetailImg.visibility = View.GONE
        }
        if(teamViewModel.study.value!!.type == 1){
            binding.fragmentTeamDetailAppBarTitle.text = "???????????????"
        }else{
            binding.fragmentTeamDetailAppBarTitle.text = "???????????????"
        }

    }
    private fun initButtons(){
        binding.fragmentTeamDetailAppBarPrev.setOnClickListener {
            this@TeamDetailFragment.findNavController().popBackStack()
        }
        binding.fragmentTeamDetailRequest.setOnClickListener {
            showRequestDialog()
        }
    }
    private fun initAdapter(){
        studyCommentAdapter = CommentAdapter(requireContext(), false)
        teamViewModel.studyComments.observe(viewLifecycleOwner){
            studyCommentAdapter.commentAllList = it
        }
        teamViewModel.studyParentComments.observe(viewLifecycleOwner){
            studyCommentAdapter.commentList = it
        }
        studyCommentAdapter.postUserId = teamViewModel.study.value!!.user!!.id
        // ??????, ????????? ?????? ?????? ?????????
        studyCommentAdapter.setAddReplyItemClickListener(object : CommentAdapter.ItemClickListener {
            override fun onClick(view: View, writerNick: String, position: Int, commentId: Int) {
                var postId = bundleOf("studyId" to studyId)
                this@TeamDetailFragment.findNavController().navigate(R.id.studyCommentFragment,postId)
            }
        })

        // ?????? ?????? ?????? ?????????
        studyCommentAdapter.setModifyItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                var postId = bundleOf("studyId" to studyId)
                this@TeamDetailFragment.findNavController().navigate(R.id.studyCommentFragment,postId)
            }
        })

        // ?????? ?????? ?????? ?????????
        studyCommentAdapter.setDeleteItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                var postId = bundleOf("studyId" to studyId)
                this@TeamDetailFragment.findNavController().navigate(R.id.studyCommentFragment,postId)
            }
        })

        // ?????? ??????????????? ?????? ????????? ?????? ?????????
        studyCommentAdapter.setSendNoteItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                mainActivity.showDialogSendMessage(writerUserId, userId)
            }
        })

        // ?????? ?????? ?????? ?????????
        studyCommentAdapter.setReportItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                mainActivity.showReportDialog(commentId, false, null, studyCommentAdapter, null, 0, false)
            }
        })

        // ????????? ?????? ?????? ?????????
        studyCommentAdapter.setReplyModifyItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                var postId = bundleOf("studyId" to studyId)
                this@TeamDetailFragment.findNavController().navigate(R.id.studyCommentFragment,postId)
            }
        })

        // ????????? ?????? ?????? ?????????
        studyCommentAdapter.setReplyDeleteItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                var postId = bundleOf("studyId" to studyId)
                this@TeamDetailFragment.findNavController().navigate(R.id.studyCommentFragment,postId)
            }
        })

        // ????????? ??????????????? ?????? ????????? ?????? ?????????
        studyCommentAdapter.setReplySendNoteItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                mainActivity.showDialogSendMessage(writerUserId, userId)
            }
        })

        // ????????? ?????? ?????? ?????????
        studyCommentAdapter.setReplyReportItemClickListener(object : CommentAdapter.MenuClickListener {
            override fun onClick(position: Int, commentId: Int, writerUserId: Int) {
                mainActivity.showReportDialog(commentId, false, null, studyCommentAdapter, null, 0, false)
            }
        })

        binding.postDetailFragmentRvComment.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = studyCommentAdapter
        }
    }
    private fun commentLayoutClickEvent(){
        binding.fragmentTeamDetailCommentInputLayout.setOnClickListener {
            var postId = bundleOf("studyId" to studyId)
            this@TeamDetailFragment.findNavController().navigate(R.id.studyCommentFragment,postId)
        }
    }
    private fun showRequestDialog(){
        var dialog = Dialog(requireContext())
        var dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_team_request,null)
        if(dialogView.parent!=null){
            (dialogView.parent as ViewGroup).removeView(dialogView)
        }
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogView.findViewById<TextView>(R.id.fragment_team_requestStudyName).text = teamViewModel.study.value!!.title
        dialog.show()

        runBlocking {
            teamViewModel.getTeamInfo()
        }
        var team = teamViewModel.teamInfo.value!!

        dialogView.findViewById<AppCompatButton>(R.id.fragment_team_requestRequst).setOnClickListener {

            runBlocking {
                userViewModel.getUser(ApplicationClass.sharedPreferencesUtil.getUser().id, 1)
            }

            if(teamViewModel.study.value!!.studyMembers?.size != null){
                if(teamViewModel.study.value!!.studyMembers!!.size == teamViewModel.study.value!!.sbLimit){
                    showCustomToast("????????? ?????????????????????. ???????????? ??? ????????????.")
                    dialog.dismiss()
                }else{
                    for(item in teamViewModel.study.value!!.studyMembers!!){
                        if(item.id == ApplicationClass.sharedPreferencesUtil.getUser().id){
                            showCustomToast("?????? ????????? ??????????????????.")
                            dialog.dismiss()
                            Log.d(TAG, "showRequestDialog: ?????? ????????? ????????????")
                        }else{
                            if(teamViewModel.study.value!!.user!!.id == ApplicationClass.sharedPreferencesUtil.getUser().id){
                                showCustomToast("????????? ??????????????? ???????????? ??? ????????????.")
                                dialog.dismiss()
                            }else{
                                if(team!!.classification == 0){
                                    if(teamViewModel.study.value!!.user!!.classroomId != userViewModel.loginUserInfo.value!!.classRoomId) {
                                        showCustomToast("?????? ?????? ???????????? ???????????? ??? ????????????.")
                                        dialog.dismiss()
                                    }else{
                                        var message = Message(
                                            "[????????? ${teamViewModel.study.value!!.id}] '${teamViewModel.study.value!!.title}?????? ?????????????????????.",
                                            0,
                                            ApplicationClass.sharedPreferencesUtil.getUser().id,
                                            teamViewModel.study.value!!.user!!.id
                                        )
                                        runBlocking {
                                            val response = MessageService().insertMessage(message)
                                            if (response.code() == 204) {
                                                showCustomToast("????????? ?????????????????????.")
                                                dialog.dismiss()
                                            }
                                        }
                                    }
                                }else{
                                    var message = Message(
                                        "[????????? ${teamViewModel.study.value!!.id}] '${teamViewModel.study.value!!.title}?????? ?????????????????????.",
                                        0,
                                        ApplicationClass.sharedPreferencesUtil.getUser().id,
                                        teamViewModel.study.value!!.user!!.id
                                    )
                                    runBlocking {
                                        val response = MessageService().insertMessage(message)
                                        if (response.code() == 204) {
                                            showCustomToast("????????? ?????????????????????.")
                                            dialog.dismiss()
                                        }
                                    }
                                }

                            }
                        }
                    }
                }

            }
        }
        dialogView.findViewById<AppCompatButton>(R.id.fragment_team_requestCancle).setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainActivity.hideBottomNavi(false)
    }
}