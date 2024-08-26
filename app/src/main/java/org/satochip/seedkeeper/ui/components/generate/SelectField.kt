package org.satochip.seedkeeper.ui.components.generate

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.satochip.seedkeeper.R
import org.satochip.seedkeeper.data.SelectFieldItem
import org.satochip.seedkeeper.ui.theme.SatoDividerPurple
import org.satochip.seedkeeper.ui.theme.SatoPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectField(
    selectList: List<SelectFieldItem>,
    onClick: (Int) -> Unit,
) {
    var isExpended by remember {
        mutableStateOf(false)
    }
    var selectedValue by remember {
        mutableStateOf(selectList[0])
    }

    ExposedDropdownMenuBox(
        expanded = !isExpended,
        onExpandedChange = {
            isExpended = !isExpended
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
                .background(
                    color = SatoPurple.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(50)
                )
                .padding(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val displayText =
                selectedValue.prefix?.let { stringResource(id = selectedValue.text, it) }
                    ?: run { stringResource(id = selectedValue.text) }
            Text(
                text = displayText,
                style = TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
            )
            Image(
                modifier = Modifier
                    .size(16.dp)
                    .graphicsLayer(rotationZ = if (isExpended) 180f else 0f),
                painter = painterResource(R.drawable.arrow),
                contentDescription = "Checked",
                colorFilter = ColorFilter.tint(Color.White)
            )
        }
        ExposedDropdownMenu(
            modifier = Modifier
                .background(
                    color = SatoPurple,
                ),
            expanded = isExpended,
            onDismissRequest = {
                isExpended = false
            },
            shape = RoundedCornerShape(24.dp)
        ) {
            selectList.forEachIndexed { index, item ->
                if (index == 0) return@forEachIndexed
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val itemText =
                                item.prefix?.let { stringResource(id = item.text, it) }
                                    ?: run { stringResource(id = item.text) }
                            Text(
                                text = itemText,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    lineHeight = 21.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            )
                            if (item == selectedValue) {
                                Image(
                                    modifier = Modifier.height(24.dp),
                                    painter = painterResource(R.drawable.checkmark),
                                    contentDescription = "Checked",
                                    colorFilter = ColorFilter.tint(Color.White)
                                )
                            }
                        }
                    },
                    onClick = {
                        selectedValue = item
                        onClick(item.text)
                        isExpended = false
                        item.prefix?.let {
                            onClick(item.prefix)
                        }
                    },
                    contentPadding = PaddingValues(vertical = 6.dp, horizontal = 24.dp)
                )
                if (index != selectList.size - 1) {
                    Spacer(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .height(1.dp)
                            .fillMaxWidth()
                            .background(SatoDividerPurple),
                    )
                }
            }
        }
    }
}