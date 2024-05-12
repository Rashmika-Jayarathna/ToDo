package com.example.todo.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.databinding.FragmentAddTaskBinding
import com.example.todo.model.Task
import com.example.todo.viewmodel.TaskViewModel
import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog


class AddTaskFragment : Fragment(R.layout.fragment_add_task),MenuProvider {

    private var addTaskBinding: FragmentAddTaskBinding?=null
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    private val binding get() =addTaskBinding!!

    private lateinit var taskViewModeI: TaskViewModel
    private lateinit var addTaskView: View



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        addTaskBinding = FragmentAddTaskBinding.inflate(inflater,container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSelectDate = view.findViewById<Button>(R.id.button_select_date)
        val buttonSelectTime = view.findViewById<Button>(R.id.button_select_time)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        taskViewModeI = (activity as MainActivity).taskViewModel
        addTaskView = view

        buttonSelectDate.setOnClickListener {
            openDatePickerDialog()
        }

        buttonSelectTime.setOnClickListener {
            openTimePickerDialog()
        }

        binding.myButton.setOnClickListener {
            // Call your function here
            saveTask(addTaskView)
        }

    }
    private fun openDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                selectedDate = "$year-${monthOfYear + 1}-$dayOfMonth"
                updateDateButton()
            },
            year,
            month,
            dayOfMonth
        )

        datePickerDialog.show()
    }

    private fun openTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.CustomDatePickerDialogTheme,
            TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                updateTimeButton()
            },
            hourOfDay,
            minute,
            false
        )

        timePickerDialog.show()
    }
    private fun updateDateButton() {
        val dateButton = view?.findViewById<Button>(R.id.button_select_date)
        dateButton?.text = selectedDate
    }
    private fun updateTimeButton() {
        val timeButton = view?.findViewById<Button>(R.id.button_select_time)
        timeButton?.text = selectedTime
    }

    private fun saveTask(view: View){
        val taskTitle = binding.addTaskTitle.text.toString().trim()
        val taskDesc = binding.addTaskDesc.text.toString().trim()
        val selectedRadioButtonId = binding.priorityRadioGroup.checkedRadioButtonId



        if(taskTitle.isNotBlank() && selectedRadioButtonId != -1){

            val selectedPriority = when (binding.priorityRadioGroup.checkedRadioButtonId) {
                R.id.radioLowPriority -> getString(R.string.low)
                R.id.radioMediumPriority -> getString(R.string.medium)
                R.id.radioHighPriority -> getString(R.string.high)
                else -> ""
            }

            val task = Task(0, taskTitle,taskDesc,selectedPriority,selectedDate,selectedTime)

            taskViewModeI.addTask(task)

            Toast.makeText(addTaskView.context, "Task Saved", Toast.LENGTH_SHORT).show()
            view.findNavController().popBackStack(R.id.homeFragment,false)


        }else{
            if (taskTitle.isBlank()) {
                Toast.makeText(addTaskView.context, "Please enter task title", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(addTaskView.context, "Please select task priority", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_add_task, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.saveMenu -> {
                saveTask(addTaskView)
                true
            }
            else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        addTaskBinding = null
    }

}