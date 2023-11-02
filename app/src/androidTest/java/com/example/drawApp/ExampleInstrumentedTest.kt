package com.example.drawApp

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
//@RunWith(AndroidJUnit4::class)
//class ExampleInstrumentedTest {
//    @Test
//    fun useAppContext() {
//        val vm = SimpleViewModel()
//        runBlocking {
//            val lifecycleOwner = TestLifecycleOwner()//Lifecycle.State.CREATED,this.coroutineContext)
//            val before = vm.color.value!!
//            var callbackFired = false
//            lifecycleOwner.run {
//                withContext(Dispatchers.Main) {
//                    vm.color.observe(lifecycleOwner) {
//                        callbackFired = true
//                    }
//                    vm.pickColor()
//                    assertTrue(callbackFired)
//                    assertNotSame(before, vm.color.value!!)
//                }
//            }
//        }
//    }
//}