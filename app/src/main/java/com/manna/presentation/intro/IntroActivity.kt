
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.manna.DeviceUtil
import com.manna.HomeActivity
import com.manna.R
import com.manna.common.BaseActivity
import com.manna.databinding.ActivityIntroBinding
import com.manna.ext.EventObserver
import com.manna.presentation.intro.IntroViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroActivity : BaseActivity<ActivityIntroBinding>(R.layout.activity_intro) {

    private val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.checkDevice(DeviceUtil.getAndroidID(this))

        viewModel.isValidDevice.observe(this, EventObserver { isValid ->
            if (isValid) {
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            } else {
                val message = if (binding.registerNameGroup.isVisible) {
                    "기기인증이 실패했어요. 다시 시도해보세요."
                } else {
                    "처음 오셨군요? 사용할 닉네임을 입력해주세요"
                }
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

                binding.registerNameGroup.isVisible = true
            }
        })

        binding.submitName.setOnClickListener {
            val name = binding.inputName.text.toString()
            if (name.isNotEmpty()) {
                viewModel.registerDevice(name, DeviceUtil.getAndroidID(this))
            }
        }

    }
}