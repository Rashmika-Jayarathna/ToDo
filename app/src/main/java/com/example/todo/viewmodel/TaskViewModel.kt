package com.example.todo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.todo.model.Task
import com.example.todo.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(app:Application, private val taskRepository: TaskRepository): AndroidViewModel(app){

    fun addTask(task: Task)=
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    fun deleteTask(task: Task)=
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    fun updateTask(task: Task)=
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }

    fun getAllTask() = taskRepository.getAllTasks()

    fun searchTask(query:String?) =
        taskRepository.searchTask(query)
}