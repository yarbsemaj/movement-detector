<?php
date_default_timezone_set("Europe/London");
function sqlcon(){
	$host_name  = "db603204515.db.1and1.com";
    $database   = "db603204515";
    $user_name  = "dbo603204515";
    $password   = "Hobnob12!";


    $connect = mysqli_connect($host_name, $user_name, $password, $database);
    
    if(mysqli_connect_errno())
    {
    echo '<p>Acsess Denied '.mysqli_connect_error().'</p>';
    }
    else
    {
    return  $connect;
    }
	
}

function missingOperand ($list,$data){
	foreach($list as $item){
	if(null==$data[$item]){return false;}	
	}
	return true;
}

function strip($link){
	foreach($_POST as $key=>$value) {
	$clean[$key]=mysqli_real_escape_string($link,$value);}
	return $clean;
}

function generateRandomString($length = 10) {
    $characters = '0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ.,?';
    $charactersLength = strlen($characters);
    $randomString = '';
    for ($i = 0; $i < $length; $i++) {
        $randomString .= $characters[rand(0, $charactersLength - 1)];
    }
    return $randomString;
}
function auth($token,$link){
	
	$query="SELECT * FROM tbl_token WHERE Token = '$token'";
	$result=mysqli_query($link, $query);
	$array=mysqli_fetch_array($result);
	if(mysqli_num_rows($result)!=1){
		return -100;
	} else
	{
		if($array['ExpiryTime']-time() < 0){
			return -101;
		}
	}
	return $array['UserID'];
	
}

function getLevel($level){
	if($level == 0) return 0; else
	return floor(log($level,2));
}
?>