@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.ncemptycomposeactivitymaterial3

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.dessertclicker.model.Dessert
import com.example.ncemptycomposeactivitymaterial3.data.Datasource
import com.example.ncemptycomposeactivitymaterial3.ui.theme.NCEmptyComposeActivityMaterial3Theme

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")
        setContent {
            NCEmptyComposeActivityMaterial3Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DessertClickerApp(desserts = Datasource.dessertList)
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume Called")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d(TAG, "onRestart Called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause Called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop Called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy Called")
    }



}

@Composable
private fun DessertClickerApp(desserts: List<Dessert>) {
    var revenue by rememberSaveable {
        mutableStateOf(0)
    }
    var dessertsSold by remember {
        mutableStateOf(0)
    }
    val currentDessertIndex by remember {
        mutableStateOf(0)
    }
    var currentDessertPrice by remember {
        mutableStateOf(desserts[currentDessertIndex].price)
    }
    var currentDessertImageId by rememberSaveable {
        mutableStateOf(desserts[currentDessertIndex].imageId)
    }

    Scaffold(topBar = {
        val intentContext = LocalContext.current
        DessertClickerAppBar(
            onShareButtonClicked = {
                shareSoldDessertsInformation(
                    intentContext, dessertsSold, revenue
                )
            }, modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        )
    }) { contentPadding ->
        DessertClickerScreen(
            revenue = revenue,
            dessertsSold = dessertsSold,
            dessertImageId = currentDessertImageId,
            onDessertClicked = {
//                update revenue
                revenue += currentDessertPrice
                dessertsSold++

//                show the next dessert
                val dessertToShow = determineDessertToShow(desserts, dessertsSold)
                currentDessertImageId = dessertToShow.imageId
                currentDessertPrice = dessertToShow.price
            }, modifier = Modifier.padding(contentPadding)
        )
    }
}

fun determineDessertToShow(
    desserts: List<Dessert>, dessertsSold: Int
): Dessert {
    var dessertToShow = desserts.first()
    for (dessert in desserts) {
        if (dessertsSold >= dessert.startProductionAmount) {
            dessertToShow = dessert
        } else {
            // The list of desserts is sorted by startProductionAmount. As you sell more desserts,
            // you'll start producing more expensive desserts as determined by startProductionAmount
            // We know to break as soon as we see a dessert who's "startProductionAmount" is greater
            // than the amount sold.
            break
        }
    }
    return dessertToShow
}


/**
 * Share desserts sold information using ACTION_SEND intent
 */
private fun shareSoldDessertsInformation(
    intentContext: Context,
    dessertsSold: Int,
    revenue: Int
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            intentContext.getString(
                R.string.share_text, dessertsSold, revenue
            )
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    try {
        ContextCompat.startActivity(intentContext, shareIntent, null)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(
            intentContext,
            intentContext.getString(R.string.sharing_not_available),
            Toast.LENGTH_LONG
        ).show()
    }
}

@Composable
fun DessertClickerAppBar(onShareButtonClicked: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.padding_medium)
            ),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge
        )
        IconButton(
            onClick = onShareButtonClicked,
            modifier = Modifier.padding(end = dimensionResource(id = R.dimen.padding_medium)),
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = stringResource(id = R.string.share),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DessertClickerScreen(
    revenue: Int,
    dessertsSold: Int,
    @DrawableRes dessertImageId: Int,
    onDessertClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Image(
            painter = painterResource(id = R.drawable.bakery_back),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = dessertImageId),
                    contentDescription = null,
                    modifier = Modifier
                        .width(
                            dimensionResource(id = R.dimen.image_size)
                        )
                        .height(dimensionResource(id = R.dimen.image_size))
                        .align(Alignment.Center)
                        .clickable { onDessertClicked() },
                    contentScale = ContentScale.Crop
                )
            }
            TransactionInfo(
                revenue = revenue,
                dessertsSold = dessertsSold,
                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    }
}

@Composable
fun TransactionInfo(revenue: Int, dessertsSold: Int, modifier: Modifier) {
    Column(modifier = modifier) {
        DessertSoldInfo(
            dessertsSold = dessertsSold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
        RevenueInfo(
            revenue = revenue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium))
        )
    }
}

@Composable
fun RevenueInfo(revenue: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = stringResource(id = R.string.total_revenue),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = "Ksh. $revenue",
            textAlign = TextAlign.Right,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun DessertSoldInfo(dessertsSold: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = stringResource(id = R.string.dessert_sold),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        Text(
            text = dessertsSold.toString(),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    NCEmptyComposeActivityMaterial3Theme {
        DessertClickerApp(Datasource.dessertList)
    }
}