<?php
require_once 'functions.php';
$link=sqlcon();

$success=true;
$operand=array("Name");
if(missingOperand($operand,$_POST)){
	$data=strip($link);
	
	$cname=$data['Name'];
	$time=time();
	
		$query="UPDATE bedmove SET Time = '$time'
		WHERE Name='$cname';";
		$result=mysqli_query($link, $query)
		or die(mysqli_error($link));
		


}else {
	$success=false;
}

	$reultrs = array("success"=>$success);
echo json_encode($reultrs);

?>