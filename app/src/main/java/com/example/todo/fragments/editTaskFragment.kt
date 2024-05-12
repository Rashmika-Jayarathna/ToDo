package com.example.todo.fragments

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todo.MainActivity
import com.example.todo.R
import com.example.todo.databinding.FragmentAddTaskBinding
import com.example.todo.databinding.FragmentEditTaskBinding
import com.example.todo.model.Task
import com.example.todo.viewmodel.TaskViewModel
import android.widget.Button
import java.util.Calendar

class editTaskFragment : Fragment(R.layout.fragment_edit_task),MenuProvider {

    private var editTaskBinding: FragmentEditTaskBinding? = null


    private val binding get() =editTaskBinding

    private lateinit var taskViewModel:TaskViewModel
    private lateinit var currentTask: Task

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    private val args: editTaskFragmentArgs by navArgs()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        editTaskBinding = FragmentEditTaskBinding.inflate(inflater,container, false)
        return binding?.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonSelectDate = view.findViewById<Button>(R.id.button_select_date)
        val buttonSelectTime = view.findViewById<Button>(R.id.button_select_time)

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this,viewLifecycleOwner, Lifecycle.State.RESUMED)

        taskViewModel = (activity as MainActivity).taskViewModel
        currentTask = args.task!!

        buttonSelectDate.setOnClickListener {
            openDatePickerDialog()
        }

        buttonSelectTime.setOnClickListener {
            openTimePickerDialog()
        }


        binding?.editTaskTitle?.setText(currentTask.taskTitle)
        binding?.editTaskDesc?.setText(currentTask.taskDesc)
        binding?.buttonSelectDate?.setText(currentTask.selectedDate)
        binding?.buttonSelectTime?.setText(currentTask.selectedTime)



        binding?.apply {
            currentTask?.let { task ->
                val priority = task.taskPrio

                // Check the priority and select the corresponding RadioButton
                when (priority) {
                    getString(R.string.low) -> radioLowPriority?.isChecked = true
                    getString(R.string.medium) -> radioMediumPriority?.isChecked = true
                    getString(R.string.high) -> radioHighPriority?.isChecked = true
                    else -> {
                        // Handle the case when priority doesn't match any RadioButton
                    }
                }
            }
        }

        binding?.editNoteFab?.setOnClickListener{
            val taskTitle = binding?.editTaskTitle?.text.toString().trim()
            val taskDesc = binding?.editTaskDesc?.text.toString().trim()
            val seDate = binding?.buttonSelectDate?.text.toString().trim()
            val seTime = binding?.buttonSelectTime?.text.toString().trim()

            val selectedRadioButtonId = binding!!.priorityRadioGroup.checkedRadioButtonId

            if(taskTitle.isNotEmpty() && selectedRadioButtonId != -1){

                val selectedPriority = when (binding!!.priorityRadioGroup.checkedRadioButtonId) {
                    R.id.radioLowPriority -> getString(R.string.low)
                    R.id.radioMediumPriority -> getString(R.string.medium)
                    R.id.radioHighPriority -> getString(R.string.high)
                    else -> ""
                }

                val task = Task(currentTask.id,taskTitle,taskDesc,selectedPriority,seDate, seTime)
                taskViewModel.updateTask(task)
                view.findNavController().popBackStack(R.id.homeFragment, false)

            }else{
                if (taskTitle.isBlank()) {
                    Toast.makeText(context, "Please enter task title", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Please select task priority", Toast.LENGTH_SHORT).show()
                }
            }
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

    private fun deleteTask(){
        AlertDialog.Builder(activity).apply {
            setTitle("Delete Task")
            setMessage("Do you want to delete this task?")
            setPositiveButton("Delete"){_,_ ->
                taskViewModel.deleteTask(currentTask)
                Toast.makeText(context, "Task deleted", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.popBackStack(R.id.homeFragment, false)
            }

            setNegativeButton("Cancel", null)

        }.create().show()
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menu.clear()
        menuInflater.inflate(R.menu.menu_edit_task,menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when(menuItem.itemId){
            R.id.deleteMenu -> {
                deleteTask()
                true

            }else -> false
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        editTaskBinding = null
    }


}