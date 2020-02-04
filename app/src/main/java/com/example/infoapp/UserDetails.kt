package com.example.infoapp

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.view.get
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.user_details.*

class UserDetails : AppCompatActivity() {
    private var originValue: String = ""
    private var originPosition: Int = 0
    private var checkedID: Int = 0
    private var osSelected: Boolean = false
    private var osValue: String = ""
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var mEditor: SharedPreferences.Editor
    private var nameValue: String? = ""
    private var ageValue: String? = ""
    private lateinit var editTextName: TextInputLayout
    private lateinit var editTextAge: TextInputLayout
    private lateinit var originSpinner: Spinner
    private lateinit var osGroup: RadioGroup
    private lateinit var userData: UserData
    private val sharePrefs: String = "com.example.infoApp"
    private val savedInstanceNameKey = "iName"
    private val savedInstanceAgeKey = "iAge"
    private val sharedPrefNameKey = "sName"
    private val sharedPrefAgeKey = "sAge"
    private val sharedPrefOriginKey = "sOrigin"
    private val sharedPrefOSKey = "sOS"
    private val defaultOrigin: String = "Choose Origin"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_details)

        editTextName = findViewById(R.id.textInputName)
        editTextAge = findViewById(R.id.textInputAge)
        originSpinner = findViewById(R.id.originSpinner)
        osGroup = findViewById(R.id.osGroup)

        /**
         * Set Values for spinner using adapter
         */
        val originAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this, R.array.origins, android.R.layout.simple_spinner_item)
        originAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        originSpinner.adapter = originAdapter
        originSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Store position of value selected in spinner
                originPosition = position
                originValue = parent?.getItemAtPosition(position).toString()
            }

        }

        /**
         * Check for radio button
         */
        osGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            osSelected = true

            //Get selected radio button
            val selectedOSButton: RadioButton = osGroup.findViewById(checkedId)
            checkedID = osGroup.indexOfChild(selectedOSButton)
            osValue = selectedOSButton.text.toString()
        }
        userData = loadPreferences()
        loadValues(userData)

        /**
         * Load values after device rotation
         */
        if(savedInstanceState != null){
            nameValue = savedInstanceState.getString(savedInstanceNameKey)
            ageValue = savedInstanceState.getString(savedInstanceAgeKey)
            editTextName.editText?.setText(nameValue)
            editTextAge.editText?.setText(ageValue)
        }
    }

    /**
     * Load user data from shared preferences(i.e in app data)
     */
    private fun loadPreferences(): UserData{
        sharedPreferences = getSharedPreferences(sharePrefs, Context.MODE_PRIVATE)
        userData = UserData()
        userData.name = sharedPreferences.getString(sharedPrefNameKey, "").toString()
        userData.age = sharedPreferences.getString(sharedPrefAgeKey, "").toString()
        userData.origin = sharedPreferences.getInt(sharedPrefOriginKey,0)
        userData.os = sharedPreferences.getInt(sharedPrefOSKey, 1)

        return userData
    }

    /**
     * Show user data from shared preferences(i.e in app data)
     */
    private fun loadValues(userData: UserData) {
        editTextName.editText?.setText(userData.name)
        editTextAge.editText?.setText(userData.age)
        originSpinner.setSelection(userData.origin)
        var savedOS: RadioButton = osGroup.getChildAt(userData.os) as RadioButton
        savedOS.isChecked = true
    }

    /**
     * Save user data in shared preferences(i.e in app data)
     */
    private fun saveData(){
        sharedPreferences = getSharedPreferences(sharePrefs, Context.MODE_PRIVATE)
        mEditor = sharedPreferences.edit()
        mEditor.putInt(sharedPrefOSKey, checkedID)
        mEditor.putString(sharedPrefNameKey, nameValue)
        mEditor.putString(sharedPrefAgeKey, ageValue)
        mEditor.putInt(sharedPrefOriginKey, originPosition)

        mEditor.apply()

    }

    /**
     * Name Validation
     */
    private fun validateName(): Boolean {
        nameValue = editTextName.editText?.text.toString()
        if(editTextName.editText?.text.toString().isEmpty()){
            textInputName.error = getString(R.string.nameEmptyErrorString)
            return false
        }else if(editTextName.editText?.text.toString().length > 15){
            textInputName.error = getString(R.string.nameLengthErrorString)
            return false
        }else{
            textInputName.error = null
            return true
        }
    }

    /**
     * Age Validation
     * */
    private fun validateAge() : Boolean {
        ageValue = editTextAge.editText?.text.toString()
        if(editTextAge.editText?.text.toString().isEmpty()){
            textInputAge.error = getString(R.string.ageEmptyErrorString)
            return false
        }else if(editTextAge.editText?.text.toString().toInt() > 100){
            textInputAge.error = getString(R.string.ageMaxErrorString)
            return false
        }else{
            textInputAge.error = null
            return true
        }
    }

    /**
     * Origin Validation
     */
    private fun validateOrigin() : Boolean {
        if(originValue == defaultOrigin){
            originError.text = getString(R.string.originErrorString)
            return false
        }else{
            originError.text = null
            return true
        }
    }

    /**
     * OS Validation
     */
    private fun validateOS() : Boolean{
        if(!osSelected){
            osError.text = getString(R.string.osErrorString)
            return false
        }else{
            osError.text = null
            return true
        }
    }

    /**
     * Check all validations
     */
    fun confirmInput(v: View) {
        val validateName: Boolean = validateName()
        val validateAge: Boolean = validateAge()
        val validateOrigin: Boolean = validateOrigin()
        val validateOS: Boolean = validateOS()

        /**
         * Show error messages if any or else save data
         */
        if(!validateName || !validateAge || !validateOrigin || !validateOS)
            return
        saveData()

        var userDetails: String = "Name: " + textInputName.editText?.text.toString()
        userDetails += "\n"
        userDetails += "Age: " + textInputAge.editText?.text.toString()
        userDetails += "\n"
        userDetails += "Origin: $originValue"
        userDetails += "\n"
        userDetails += "OS: $osValue"
        userDetails += "\n"
        userDetails += "Data Saved!"
        Toast.makeText(this,userDetails,Toast.LENGTH_LONG).show()
    }

    /**
     * Save activity state in case of transition in device rotation
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(savedInstanceNameKey, editTextName.editText?.text.toString())
        outState.putString(savedInstanceAgeKey, editTextAge.editText?.text.toString())
    }

}
