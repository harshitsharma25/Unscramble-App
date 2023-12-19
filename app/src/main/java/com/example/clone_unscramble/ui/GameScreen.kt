package com.example.clone_unscramble.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.clone_unscramble.R
import com.example.clone_unscramble.ui.theme.Clone_unscrambleTheme

@Composable
fun GameScreen(gameViewModel: GameViewModel = viewModel()){

   val mediumPadding = dimensionResource(id = R.dimen.padding_medium)
   val gameUiState by gameViewModel.uiState.collectAsState()  // collectAsState means fetching the current state of ui.

    Column(
        modifier = Modifier
            .padding(mediumPadding)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        // app name
        Text(
            text = stringResource(id = R.string.app_name),
            style = typography.titleLarge
        )


        // card Layout before the submit button
       CardLayout(
           modifier = Modifier
               .fillMaxWidth()
               .wrapContentHeight()
               .padding(mediumPadding),

           CurrentscrambledWord = gameUiState.currentScrambledWord,
           wordCount = gameUiState.currentWordCount,
           userGuess = gameViewModel.userGuess,
           onUserGuessChanged = { gameViewModel.UpdateUserGuess(it) },  // this will come in lamda function.
           isGuessWrong = gameUiState.isGuessWordWrong,
           onKeyboardDone = { gameViewModel.checkUserGuess() },        // this will come in lamda function.

       )


        // submit and skip word and score
       Column (
           modifier = Modifier
               .padding(mediumPadding)
               .fillMaxWidth(),
           verticalArrangement = Arrangement.Center,
           horizontalAlignment = Alignment.CenterHorizontally
       ){
           // submit button
           Button(
               modifier = Modifier

                   .fillMaxWidth(),
               onClick = { gameViewModel.checkUserGuess() }
           ){
               Text (
                   text = stringResource(id = R.string.submit),
                   fontSize = 16.sp
               )
           }

           // outlined skip button
           OutlinedButton(
               modifier = Modifier
                   .fillMaxWidth(),
               onClick = { gameViewModel.skipWord() })
           {
               Text(
                   text = stringResource(id = R.string.skip),
                   fontSize = 16.sp
               )
           }

           // defined by me for score
           GameScoreCard(
               score = gameUiState.score,
               modifier = Modifier.padding(20.dp),
           )

           if(gameUiState.isGameOver){
               FinalScoreDialog(score = gameUiState.score,onPlayAgain = { gameViewModel.resetGame()})
           }
       }
    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
){
    val activity =(LocalContext.current as Activity)


    AlertDialog(
        onDismissRequest = {   },
        title = { Text(text = stringResource(R.string.congrats))},
        text = {
            Text(
                text = stringResource(R.string.you_scored,score),
                style = typography.titleMedium
            )

               },
        modifier = modifier,
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = stringResource(id = R.string.play_again))
            }
        },
        dismissButton = {
            TextButton(onClick = {
                activity.finish()
             }
            ) {
                Text(text = stringResource(id = R.string.exit))
            }
        },

    )
}

@Composable
fun GameScoreCard(score:Int,modifier: Modifier = Modifier){
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
    ){
        Text(
            text = stringResource(id = R.string.score,score),
            style = typography.headlineMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Composable
fun CardLayout(
    modifier: Modifier,
    CurrentscrambledWord : String,
    wordCount :Int,
    userGuess: String,
    onUserGuessChanged : (String) -> Unit,
    isGuessWrong : Boolean,
    onKeyboardDone : () -> Unit,
){
    val mediumPadding = dimensionResource(id = R.dimen.padding_medium)
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ){
        Column (
            verticalArrangement = Arrangement.spacedBy(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(mediumPadding)
        ){
            Text(
                text = stringResource(id = R.string.word_count,wordCount),
                style = typography.titleMedium,
                modifier = Modifier
                    .clip(shapes.medium)
                    .align(alignment = Alignment.End)
                    .background(colorScheme.surfaceTint)
                    .padding(horizontal = 10.dp, vertical = 2.dp),
                color = colorScheme.onPrimary
            )


            Text(
                text = CurrentscrambledWord ,
                style = typography.displayMedium
            )

            Text(
                text = stringResource(id = R.string.instructions),
                style = typography.titleMedium
            )


            OutlinedTextField(
                isError = isGuessWrong,
                value = userGuess,
                singleLine = true,
                shape = shapes.large,
                onValueChange = onUserGuessChanged,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    if(!isGuessWrong)
                        Text(stringResource(R.string.enter_word))
                    else
                        Text(stringResource(R.string.wrong_guess))
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onKeyboardDone()
                    }
                )

            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun GameScreenPreview(){
    Clone_unscrambleTheme {
        GameScreen()
    }
}
