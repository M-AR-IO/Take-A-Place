package my.mobile.takeaplace.ui.user

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import my.mobile.takeaplace.R
import my.mobile.takeaplace.databinding.FragmentDaftarBinding

class DaftarFragment : Fragment() {

    private lateinit var daftarViewModel: DaftarViewModel
    private var _binding: FragmentDaftarBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDaftarBinding.inflate(inflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        daftarViewModel = ViewModelProvider(this,DaftarViewModelFactory(requireContext()))
            .get(DaftarViewModel::class.java)

        val usernameEditText = binding.username
        val emailEditText = binding.email
        val passwordEditText = binding.password
        val daftarButton = binding.daftar
        val loadingProgressBar = binding.loading

        usernameEditText.requestFocus()

        daftarViewModel.daftarFormState.observe(viewLifecycleOwner,
            Observer { daftarFormState ->
                daftarFormState ?: return@Observer

                daftarButton.isEnabled = daftarFormState.isDataValid
                daftarFormState.emailError?.let {
                    emailEditText.error = getString(it)
                }
                daftarFormState.usernameError?.let {
                    usernameEditText.error = getString(it)
                }
                daftarFormState.passwordError?.let {
                    passwordEditText.error = getString(it)
                }
            }
        )
        daftarViewModel.daftarResult.observe(viewLifecycleOwner,
            Observer { daftarResult ->
                daftarResult ?: return@Observer

                loadingProgressBar.visibility = View.GONE
                daftarResult.error?.let {
                    showDaftarFailed(it,daftarResult.errorMessage)
                }
                daftarResult.success?.let {
                    updateUiWithUser(it)
                }
            }
        )
        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                daftarViewModel.daftarDataChanged(
                    emailEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
        }
        emailEditText.addTextChangedListener(afterTextChangedListener)
        usernameEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.addTextChangedListener(afterTextChangedListener)
        passwordEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.visibility = View.VISIBLE
                daftarViewModel.daftar(
                    emailEditText.text.toString(),
                    usernameEditText.text.toString(),
                    passwordEditText.text.toString()
                )
            }
            false
        }

        daftarButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            daftarViewModel.daftar(
                emailEditText.text.toString(),
                usernameEditText.text.toString(),
                passwordEditText.text.toString()
            )
        }
    }
    private fun updateUiWithUser(model: RegisteredUser) {
        val welcome = getString(R.string.welcome) + model.username
        // TODO : initiate successful logged in experience
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, welcome, Toast.LENGTH_LONG).show()


        // Pindah ke fragment login
        val action = DaftarFragmentDirections.actionDaftarfragmentToLoginfragment(model.username)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun showDaftarFailed(@StringRes errorString: Int, pesan: String) {
        val appContext = context?.applicationContext ?: return
        var teks = resources.getString(errorString)
        if (pesan != ""){
            teks += "\n" + pesan
        }
        Toast.makeText(appContext, teks, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}