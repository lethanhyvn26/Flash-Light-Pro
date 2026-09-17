package flashlight.phichung.com.torch.camera.data.local

data class User(
    val usePinchToZoom: Boolean,
    val useTapToFocus: Boolean,
    val useCamFront: Boolean,
) {
    companion object {
        val Default = User(usePinchToZoom = true, useTapToFocus = true, useCamFront = false)
    }
}