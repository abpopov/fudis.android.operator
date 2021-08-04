package thapl.com.fudis.utils

import android.text.method.PasswordTransformationMethod
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import java.util.regex.Pattern

interface Validate {
    fun completeValidate(): Boolean
    fun processValidate(): Boolean
    var callback: () -> Unit
}

class ViewsValidator(private val views: List<Validate>, callback: (Boolean) -> Unit) : Validate {

    init {
        views.forEach {
            it.callback = {
                callback.invoke(completeValidate())
            }
        }
    }

    override fun completeValidate(): Boolean {
        return views.any { it.completeValidate().not() }.not()
    }

    override fun processValidate() = false

    override var callback = {}

}

class EmailValidator(
    private val view: EditText?
) : Validate {

    override var callback = {}

    init {
        view?.doAfterTextChanged {
            callback.invoke()
        }
    }

    override fun completeValidate(): Boolean {
        return view?.text?.toString().isNullOrEmpty().not()
        //return android.util.Patterns.EMAIL_ADDRESS.matcher(view?.text.toString()).matches()
    }

    override fun processValidate(): Boolean {
        return view?.text?.toString().isNullOrEmpty()
    }

}

class PasswordValidator(
    private val view: EditText?
) : Validate {

    override var callback = {}

    private var visible = false

    init {
        view?.transformationMethod = PasswordTransformationMethod.getInstance()
        view?.doAfterTextChanged {
            callback.invoke()
        }
    }

    private fun isValidPasswordFormat(password: String): Boolean {
        val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
        val passwordREGEX =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,24}$")
        /*val passwordREGEX = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#%^&+-_=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$")*/
        //return passwordREGEX.matcher(password).matches()
        return view?.text?.toString().isNullOrEmpty().not()
    }

    override fun completeValidate(): Boolean {
        return isValidPasswordFormat(view?.text.toString())
    }

    override fun processValidate(): Boolean {
        return view?.text?.toString().isNullOrEmpty()
    }

}