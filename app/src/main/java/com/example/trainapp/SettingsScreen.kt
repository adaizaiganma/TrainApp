package com.example.trainapp

import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val app = TrainApp.instance

    // --- State for Dialogs ---
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showCredentialsDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }

    // --- State for Language Name ---
    val currentLocale = AppCompatDelegate.getApplicationLocales().toLanguageTags()
    val currentLanguageName = when {
        currentLocale.contains("zh") -> "繁體中文"
        else -> "English"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.settings_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // --- User Name Section ---
        SettingsItem(
            icon = Icons.Default.Person,
            title = "Username",
            value = app.userName.ifBlank { "Not set" },
            onClick = { showNameDialog = true }
        )

        // --- TDX Credentials Section ---
        SettingsItem(
            icon = Icons.Default.VpnKey,
            title = stringResource(R.string.settings_tdx_credentials),
            value = "",
            onClick = { showCredentialsDialog = true }
        )

        // --- Language Section ---
        SettingsItem(
            icon = Icons.Default.Language,
            title = stringResource(R.string.settings_language),
            value = currentLanguageName,
            onClick = { showLanguageDialog = true }
        )
    }

    // Name Dialog
    if (showNameDialog) {
        var tempName by remember { mutableStateOf(app.userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            title = { Text("Set Username") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Username") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    app.userName = tempName
                    showNameDialog = false
                }) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    // Credentials Dialog
    if (showCredentialsDialog) {
        var tempClientId by remember { mutableStateOf(app.clientId) }
        var tempClientSecret by remember { mutableStateOf(app.clientSecret) }

        AlertDialog(
            onDismissRequest = { showCredentialsDialog = false },
            title = { Text(stringResource(R.string.settings_tdx_credentials)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tempClientId,
                        onValueChange = { tempClientId = it },
                        label = { Text(stringResource(R.string.label_client_id)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempClientSecret,
                        onValueChange = { tempClientSecret = it },
                        label = { Text(stringResource(R.string.label_client_secret)) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    app.clientId = tempClientId
                    app.clientSecret = tempClientSecret
                    TrainRepository.resetToken()
                    showCredentialsDialog = false
                    Toast.makeText(context, context.getString(R.string.msg_credentials_saved), Toast.LENGTH_SHORT).show()
                }) {
                    Text(stringResource(R.string.btn_save))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCredentialsDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }

    // Language Selection Dialog
    if (showLanguageDialog) {
        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            title = { Text(stringResource(R.string.dialog_select_language)) },
            text = {
                Column {
                    LanguageOption("English", currentLanguageName) {
                        setLanguage("en")
                        showLanguageDialog = false
                    }
                    LanguageOption("繁體中文", currentLanguageName) {
                        setLanguage("zh-TW")
                        showLanguageDialog = false
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.btn_cancel))
                }
            }
        )
    }
}

private fun setLanguage(languageCode: String) {
    val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
    AppCompatDelegate.setApplicationLocales(appLocale)
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    value: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(28.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
}

@Composable
fun LanguageOption(
    text: String,
    selectedLanguage: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (text == selectedLanguage),
            onClick = onClick
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}
