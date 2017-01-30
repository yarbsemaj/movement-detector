<?php

require_once 'functions.php';
$link=sqlcon();

$success=true;
$operand=array("Name");
if(missingOperand($operand,$_POST)){
	$data=strip($link);
	
	$name=$data['Name'];
	
	$query="SELECT * FROM bedmove
		WHERE Name='$name';";
	
	$query = mysqli_query($link,$query);
	$results=mysqli_fetch_array($query);
	if(mysqli_num_rows($query)==0){
		$success=false;
	}
	
}else {
	$success=false;
}

if($success){
	$lastUpdate=$results['Time'];
	$delay=$results['Delay'];
	$alert= (time()-$lastUpdate)>$delay;
	$data=array("success"=>$success, "alert"=> $alert, "delay" => $delay , lastUpdate => $lastUpdate);
}else
{
	$data = array("success"=>$success);
}
echo json_encode($data);
?>