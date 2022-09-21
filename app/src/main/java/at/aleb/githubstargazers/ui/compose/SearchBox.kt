package at.aleb.githubstargazers.ui.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction

@Composable
fun SearchBox(
    label: String,
    term: String,
    onChange: (String) -> Unit,
    onSearch: (String) -> Unit
) {
    Row {
        TextField(
            label = {
                Text(label)
            },
            value = term,
            onValueChange = onChange,
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { onSearch(term) })
        )
    }
}