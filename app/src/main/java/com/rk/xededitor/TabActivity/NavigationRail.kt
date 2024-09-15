package com.rk.xededitor.TabActivity

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rk.libcommons.ActionPopup
import com.rk.libcommons.LoadingPopup
import com.rk.xededitor.R
import com.rk.xededitor.Settings.Keys
import com.rk.xededitor.Settings.SettingsData
import com.rk.xededitor.TabActivity.file.ProjectManager
import com.rk.xededitor.git.git
import com.rk.xededitor.rkUtils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import java.io.File

object NavigationRail {
  @SuppressLint("SetTextI18n")
  fun setupNavigationRail(activity: TabActivity) {
    with(activity) {
      
      val openFileId = View.generateViewId()
      val openDirId = View.generateViewId()
      val openPathId = View.generateViewId()
      val privateFilesId = View.generateViewId()
      val cloneRepo = View.generateViewId()
      val pluginDir = View.generateViewId()
      
      var dialog: AlertDialog? = null
      
      val listener = View.OnClickListener { v ->
        when (v.id) {
          openFileId -> {
            fm.requestOpenFile()
          }
          
          openDirId -> {
            fm.requestOpenDirectory()
          }
          
          openPathId -> {
            fm.requestOpenFromPath()
          }
          
          privateFilesId -> {
            ProjectManager.addProject(this, filesDir.parentFile!!)
          }
          
          pluginDir -> {
          
          }
          
          cloneRepo -> {
            val view = LayoutInflater.from(this@with).inflate(R.layout.popup_new, null)
            view.findViewById<LinearLayout>(R.id.mimeTypeEditor).visibility = View.VISIBLE
            val repoLinkEdit = view.findViewById<EditText>(R.id.name).apply {
              hint = "https://github.com/UserName/repo.git"
            }
            val branchEdit = view.findViewById<EditText>(R.id.mime).apply {
              hint = "Branch. Example: main"
              setText("main")
            }
            MaterialAlertDialogBuilder(this).setTitle("Clone repository").setView(view)
              .setNegativeButton("Cancel", null).setPositiveButton("Apply") { _, _ ->
                val repoLink = repoLinkEdit.text.toString()
                val branch = branchEdit.text.toString()
                val repoName = repoLink.substringAfterLast("/").removeSuffix(".git")
                val repoDir = File(
                  "${
                    SettingsData.getString(
                      Keys.GIT_REPO_DIR,
                      "/storage/emulated/0"
                    )
                  }/$repoName"
                )
                
                
                
                if (repoLink.isEmpty() || branch.isEmpty()) {
                  rkUtils.toast(this, "Please fill in both fields")
                  return@setPositiveButton
                } else if (repoDir.exists()) {
                  rkUtils.toast(this, "$repoDir already exists!")
                  return@setPositiveButton
                }
                
                
                val loadingPopup = LoadingPopup(this, null).setMessage("Cloning repository...")
                loadingPopup.show()
                
                
                
                git.clone(repoLink, branch, repoDir) { status, exception ->
                  lifecycleScope.launch(Dispatchers.Main) {
                    if (status == git.RESULT.OK) {
                      ProjectManager.addProject(this@with, repoDir)
                      return@launch
                    }
                    
                    exception?.let {
                      it.printStackTrace()
                      
                      if (status != git.RESULT.ERROR){
                        return@launch
                      }
                      
                      val credentials = SettingsData.getString(Keys.GIT_CRED, "").split(":")
                      if (credentials.size != 2) {
                        rkUtils.toast(this@with, "Repository is private. Check your credentials")
                        return@launch
                      }
                      
                      git.clone(
                        repoLink,
                        branch,
                        repoDir,
                        credentials[0],
                        credentials[1]
                      ) { status, exception1 ->
                        if (status == git.RESULT.ERROR){
                          rkUtils.toast(this@with,"Error : ${exception1?.message}")
                        }
                        exception1?.printStackTrace()
                      }
                    }
                    
                    loadingPopup.hide()
                  }
                }
                
              }.show()
          }
        }
        dialog?.hide()
        dialog = null
      }
      
      fun handleAddNew() {
        ActionPopup(this).apply {
          addItem(
            "Open a Directory",
            "Choose a directory as a project",
            ContextCompat.getDrawable(this@with, R.drawable.outline_folder_24),
            listener,
            openDirId
          )
          addItem(
            "Open a File",
            "Choose a file to directly edit it",
            ContextCompat.getDrawable(this@with, R.drawable.outline_insert_drive_file_24),
            listener,
            openFileId
          )
          addItem(
            "Open from Path",
            "Open a project/file from a path",
            ContextCompat.getDrawable(this@with, R.drawable.android),
            listener,
            openPathId
          )
          addItem(
            "Clone repository",
            "Clone repository using Git",
            ContextCompat.getDrawable(this@with, R.drawable.git),
            listener,
            cloneRepo
          )
          addItem(
            "Plugins",
            "Plugins Directory",
            ContextCompat.getDrawable(this@with, R.drawable.extension),
            listener,
            pluginDir
          )
          addItem(
            "Private Files",
            "Private files of karbon",
            ContextCompat.getDrawable(this@with, R.drawable.android),
            listener,
            privateFilesId
          )
         
          
          
          setTitle("Add")
          getDialogBuilder().setNegativeButton("Cancel", null)
          dialog = show()
        }
        
      }
      
      binding.navigationRail.setOnItemSelectedListener { item ->
        if (item.itemId == R.id.add_new) {
          handleAddNew()
          false
        } else {
          ProjectManager.projects[item.itemId]?.let {
            ProjectManager.changeProject(File(it), this)
          }
          true
        }
      }
      
      //close drawer if same item is selected again except add_new item
      binding.navigationRail.setOnItemReselectedListener { item ->
        if (item.itemId == R.id.add_new) {
          handleAddNew()
        }
      }
      
    }
  }
}