package flashlight.phichung.com.torch.utils

import timber.log.Timber

class ReleaseTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        return  // no need to log in release build
    }
}