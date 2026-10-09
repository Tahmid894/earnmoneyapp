package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.RewardRepository
import com.example.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun readStringFromContext() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Watch & Earn", appName)
  }

  @Test
  fun rewardRepositoryPointsFlow() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = RewardRepository(context)
    repo.resetDataForTesting()

    assertEquals(250, repo.getPoints())
    repo.addPoints(50)
    assertEquals(300, repo.getPoints())

    // Deduct 100 points
    val success = repo.deductPoints(100)
    assertTrue(success)
    assertEquals(200, repo.getPoints())
  }
}
