package br.senai.jandira.sp.apilivraria.components.screen



import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.apilivraria.R
import com.google.firebase.Firebase
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream


@Composable
fun foto(){

    var showDialog by remember {
        mutableStateOf(false)
    }

    //  com o Firebase
    val isUploading = remember {
        mutableStateOf(false)
    }
    val context = LocalContext.current
    val img: Bitmap = BitmapFactory.decodeResource(Resources.getSystem(),android.R.drawable.ic_menu_gallery)
    val bitmap = remember{ mutableStateOf(img) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ){
        if (it!= null){

            bitmap.value = it
        }

    }

    val launcherImage = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ){
        if (Build.VERSION.SDK_INT < 28){
            bitmap.value = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
        }
        else{
            val  source = it?.let {
                    it1 -> ImageDecoder.createSource(context.contentResolver, it1)
            }
            bitmap.value = source?.let { it1 -> ImageDecoder.decodeBitmap(it1)}!!

        }    }




Surface(
modifier = Modifier.fillMaxSize()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
            .background(color = Color.Cyan),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            bitmap = bitmap.value.asImageBitmap(),
            contentDescription = "FOTO",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .size(250.dp)
                .background(color = Color.Blue)
                .border(
                    width = 1.dp,
                    color = Color.White,
                    shape = CircleShape

                )
        )
    }
    Box(
        modifier = Modifier
            .padding(top = 220.dp, start = 260.dp)
    ){
        Image(
            painter = painterResource(id = R.drawable.baseline_photo_camera_24),
            contentDescription ="Icone de Imagem",
            modifier = Modifier
                .clip(CircleShape)
                .background(Color.White)
                .size(50.dp)
                .padding(10.dp)
                .clickable {
                    showDialog = true
                }
        )
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 180.dp)
    ) {
        Button(onClick = {
            isUploading.value =true
            bitmap.value.let {
                    bitmap ->
                UploadingImageToFireBase(bitmap,context as ComponentActivity){
                        sucess -> isUploading.value = false
                    if (sucess){
                        Toast.makeText(context, "Upaload Sucessofuy", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "Falid to Upaload", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        },
            colors = ButtonDefaults.buttonColors(
                Color.Black
            )
        ) {
            Text(
                text = "Upload Image",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (showDialog){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .width(300.dp)
                    .height(100.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Blue)
            ) {
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(
                        text = "X",
                        color = Color.White,
                        modifier = Modifier
                            .clickable { showDialog = false }
                    )

                }
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                launcherImage.launch("image/*")
                                showDialog = false
                            }
                    )
                    Text(
                        text = "Galerya",
                        color = Color.White
                    )
                }

                Column(
                    modifier = Modifier.padding(start = 60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                        contentDescription = "",
                        modifier = Modifier
                            .size(50.dp)
                            .clickable {
                                launcher.launch()
                                showDialog = false
                            }
                    )

                    Text(
                        text = "Camera",
                        color = Color.White
                    )
                }
            }
        }
    }
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .height(450.dp)
            .fillMaxWidth()
    ) {

        if (isUploading.value){
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(16.dp),
                color = Color.White
            )
        }

    }
}
}


//Função de Upload

fun UploadingImageToFireBase(bitmap: Bitmap, context: ComponentActivity, callback: (Boolean) -> Unit) {

    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("images/${bitmap}")

    val baos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)

    val imageData = baos.toByteArray()

    imageRef.putBytes(imageData).addOnSuccessListener {
        callback(true)
    }.addOnFailureListener {
        callback(false)
    }
}