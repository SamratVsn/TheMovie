package com.example.themovieapp.ui.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.themovieapp.R
import com.example.themovieapp.data.UserPreferences
import com.example.themovieapp.data.auth.AuthState

private val HeaderHeight = 140.dp
private val AvatarSize = 96.dp

@Composable
fun ProfileScreen(
    onAuthClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
) {
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val authState by viewModel.authState.collectAsStateWithLifecycle()

    val actions = remember(viewModel, onAuthClick, onSettingsClick) {
        ProfileActions(
            onSignInClick = onAuthClick,
            onSignOutConfirmed = viewModel::signOut,
            onSettingsClick = onSettingsClick,
        )
    }

    ProfileScreenContent(
        preferences = preferences,
        authState = authState,
        actions = actions,
        modifier = modifier,
    )
}

@Immutable
data class ProfileActions(
    val onSignInClick: () -> Unit,
    val onSignOutConfirmed: () -> Unit,
    val onSettingsClick: () -> Unit,
)

private fun resolveDisplayName(
    preferences: UserPreferences,
    authState: AuthState,
): String {
    val authName = (authState as? AuthState.SignedIn)?.user?.displayName?.takeIf { it.isNotBlank() }
    if (authName != null) return authName
    return preferences.displayName
}

@Composable
fun ProfileScreenContent(
    preferences: UserPreferences,
    authState: AuthState,
    actions: ProfileActions,
    modifier: Modifier = Modifier,
) {
    val displayName = resolveDisplayName(preferences, authState)
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 12.dp)
                )
                androidx.compose.material3.IconButton(onClick = actions.onSettingsClick) {
                    Icon(
                        painter = painterResource(R.drawable.settings),
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            ProfileHeader(displayName = displayName)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileDisplayContent(
                    preferences = preferences,
                    displayName = displayName,
                    authState = authState,
                    actions = actions,
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(displayName: String) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(HeaderHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        )
                    )
                )
        )
        Avatar(
            displayName = displayName,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = AvatarSize / 2)
        )
    }
    Spacer(Modifier.height(AvatarSize / 2 + 16.dp))
}

@Composable
private fun Avatar(displayName: String, modifier: Modifier = Modifier) {
    val description = stringResource(R.string.profile_picture)
    Box(
        modifier = modifier
            .size(AvatarSize)
            .background(MaterialTheme.colorScheme.background, CircleShape)
            .padding(4.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        val initials = initialsOf(displayName)
        if (initials.isEmpty()) {
            Icon(
                painter = painterResource(R.drawable.profile),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(44.dp)
            )
        } else {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

private fun initialsOf(name: String): String =
    name.trim()
        .split(Regex("\\s+"))
        .filter { it.isNotEmpty() }
        .take(2)
        .joinToString("") { it.first().uppercaseChar().toString() }

@Composable
private fun ProfileDisplayContent(
    preferences: UserPreferences,
    displayName: String,
    authState: AuthState,
    actions: ProfileActions,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = displayName.ifBlank { stringResource(R.string.guest_name) },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        if (authState is AuthState.SignedIn && !authState.user.email.isNullOrBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = authState.user.email,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Spacer(Modifier.height(8.dp))

        val hasBio = preferences.bio.isNotBlank()
        Text(
            text = if (hasBio) preferences.bio else stringResource(R.string.no_bio_yet),
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = if (hasBio) FontStyle.Normal else FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(Modifier.height(28.dp))

        SectionLabel(R.string.preferences)
        ProfileInfoCard(preferences)

        Spacer(Modifier.height(24.dp))

        SectionLabel(R.string.account)
        AccountCard(
            authState = authState,
            onSignInClick = actions.onSignInClick,
            onSignOutConfirmed = actions.onSignOutConfirmed,
        )

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionLabel(@StringRes textRes: Int) {
    Text(
        text = stringResource(textRes),
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 8.dp)
    )
}

@Composable
private fun ProfileInfoCard(preferences: UserPreferences) {
    val themeLabel = preferences.themeMode.name.lowercase().replaceFirstChar { it.titlecase() }
    val items = listOf(
        Triple(R.drawable.favorite, R.string.favorite_genre, preferences.favoriteGenre),
        Triple(R.drawable.home, R.string.default_category, preferences.defaultCategory.label),
        Triple(R.drawable.palette, R.string.theme, themeLabel),
    )

    SurfaceCard {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            items.forEachIndexed { index, (icon, label, value) ->
                ProfileInfoRow(
                    icon = icon,
                    label = stringResource(label),
                    value = value.ifBlank { "—" },
                )
                if (index < items.lastIndex) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    @DrawableRes icon: Int,
    label: String,
    value: String,
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
private fun AccountCard(
    authState: AuthState,
    onSignInClick: () -> Unit,
    onSignOutConfirmed: () -> Unit,
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    SurfaceCard {
        Column(modifier = Modifier.padding(16.dp)) {
            when (authState) {
                AuthState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                AuthState.SignedOut -> {
                    Text(
                        text = stringResource(R.string.guest_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.guest_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = onSignInClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.login),
                            contentDescription = null,
                            modifier = Modifier.size(ButtonDefaults.IconSize)
                        )
                        Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                        Text(stringResource(R.string.sign_in))
                    }
                }

                is AuthState.SignedIn -> {
                    ProfileInfoRow(
                        icon = R.drawable.profile,
                        label = stringResource(R.string.signed_in_as),
                        value = authState.user.email
                            ?: authState.user.displayName
                            ?: stringResource(R.string.guest_name),
                    )
                    Spacer(Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { showSignOutDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.error.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(stringResource(R.string.sign_out))
                    }
                }
            }
        }
    }

    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            title = { Text(stringResource(R.string.sign_out_confirm_title)) },
            text = { Text(stringResource(R.string.sign_out_confirm_body)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSignOutDialog = false
                        onSignOutConfirmed()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.sign_out),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
private fun SurfaceCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
    ) {
        content()
    }
}
