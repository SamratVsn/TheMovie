package com.example.themovieapp.ui.profile

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.themovieapp.R
import com.example.themovieapp.data.UserPreferences

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory)
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val editState by viewModel.editState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(
                displayName = preferences.displayName,
                isEditing = editState.isEditing
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-40).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (editState.isEditing) {
                    Spacer(Modifier.height(52.dp))
                    EditProfileForm(viewModel = viewModel, editState = editState)
                } else {
                    Spacer(Modifier.height(52.dp))
                    ProfileDisplayContent(
                        preferences = preferences,
                        onEditClick = viewModel::startEditing
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(displayName: String, isEditing: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isEditing) "Editing profile" else displayName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = (-40).dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.profile),
                contentDescription = "Profile picture",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(44.dp)
            )
        }
    }
}

@Composable
private fun ProfileDisplayContent(
    preferences: UserPreferences,
    onEditClick: () -> Unit
) {
    Text(
        text = preferences.displayName,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(Modifier.height(6.dp))

    Text(
        text = preferences.bio.ifBlank { "No bio yet." },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 8.dp)
    )

    Spacer(Modifier.height(24.dp))

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            ProfileInfoRow(
                icon = R.drawable.favotite,
                label = "Favorite genre",
                value = preferences.favoriteGenre
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileInfoRow(
                R.drawable.home,
                label = "Default category",
                value = preferences.defaultCategory.label
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileInfoRow(
                icon = R.drawable.palette,
                label = "Theme",
                value = preferences.themeMode.name.lowercase()
                    .replaceFirstChar { it.titlecase() }
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Button(
        onClick = onEditClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Icon(painterResource(R.drawable.edit_task), contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text("Edit profile")
    }

    Spacer(Modifier.height(24.dp))
}

@Composable
private fun ProfileInfoRow(
    @DrawableRes icon: Int,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EditProfileForm(
    viewModel: ProfileViewModel,
    editState: ProfileEditState
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val bioFocusRequester = remember { FocusRequester() }
    val genreFocusRequester = remember { FocusRequester() }
    val isNameValid = editState.nameDraft.isNotBlank()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            OutlinedTextField(
                value = editState.nameDraft,
                onValueChange = viewModel::onNameChange,
                label = { Text("Display name") },
                singleLine = true,
                isError = !isNameValid,
                supportingText = { if (!isNameValid) Text("Name can't be empty") },
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { bioFocusRequester.requestFocus() }),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = editState.bioDraft,
                onValueChange = viewModel::onBioChange,
                label = { Text("Bio") },
                minLines = 3,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { genreFocusRequester.requestFocus() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(bioFocusRequester)
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value = editState.genreDraft,
                onValueChange = viewModel::onGenreChange,
                label = { Text("Favorite genre") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(genreFocusRequester)
            )
        }
    }

    Spacer(Modifier.height(20.dp))

    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = viewModel::cancelEditing,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Cancel")
        }
        Button(
            onClick = viewModel::saveProfile,
            enabled = isNameValid && !editState.isSaving,
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            if (editState.isSaving) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Save")
            }
        }
    }

    Spacer(Modifier.height(24.dp))
}