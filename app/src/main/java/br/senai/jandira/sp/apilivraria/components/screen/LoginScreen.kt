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
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.senai.jandira.sp.apilivraria.R
import br.senai.jandira.sp.apilivraria.components.DefaultButton
import br.senai.jandira.sp.apilivraria.components.DefaultTextField
import br.senai.jandira.sp.apilivraria.components.fotoFireBase


@Composable
fun loginScreen(){


    //Variaveis

    //    Modal Galerya & Foto
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

        }
    }


    //Variaveis de estado
    var emailState by remember{
        mutableStateOf("")
    }

    var SenhaState by remember{
        mutableStateOf("")
    }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(10.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            //Imagem
            Row(
                modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
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
                Image(
                    painter = painterResource(id = R.drawable.baseline_photo_camera_24),
                    contentDescription ="Icone de Imagem",
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .size(50.dp)
                        .padding(10.dp)
                        .clickable {
                            launcherImage.launch("image/*")
                            showDialog = true
                        }
                )
            }
        }


            Spacer(modifier = Modifier.height(25.dp))

            //Email e Senha
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                DefaultTextField(
                        valor = emailState,
                label = "E-mail",
                onValueChange ={
                    emailState = it
                } )

                DefaultTextField(
                    valor = SenhaState,
                    label = "Senha",
                    onValueChange ={
                        SenhaState = it
                    } )
            //Botão
            Row(
                modifier = Modifier
                    .height(200.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.Bottom
            ) {
                Button(
                    onClick = {
                        isUploading.value = true
                        bitmap.value.let { bitmap ->
                            br.senai.jandira.sp.apilivraria.components.UploadingImageToFireBase(
                                bitmap,
                                context as ComponentActivity
                            ) { sucess ->
                                isUploading.value = false
                                if (sucess) {
                                    Toast.makeText(
                                        context,
                                        "Upaload Sucessofuy",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Falid to Upaload", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        Color.Black
                    )
                ) {
                    Text(
                        text = "Entrar",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            }
        }
    }



@Preview
@Composable
fun loginscreenPreview(){

    loginScreen()

}


