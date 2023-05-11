
import android.view.LayoutInflater
import android.view.ViewGroup

class NewOrderFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        return inflater.inflate(R.layout.new_order_fragment, container, false)
    }
}