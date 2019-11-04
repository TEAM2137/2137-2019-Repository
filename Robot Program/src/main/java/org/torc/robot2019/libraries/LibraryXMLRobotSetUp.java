package org.torc.robot2019.libraries;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LibraryXMLRobotSetUp {

    private double dblMotorGearReduction = 0;
    private double dblDriveGearReduction = 0;
    private double dblWheelDiameter = 4;
    private double dblMaxVelocity = 5600;
    
    private int mintLeftMotor1ID = 0;
    private int mintLeftMotor2ID = 0;
    private int mintRightMotor1ID = 0;
    private int mintRightMotor2ID = 0;

    private boolean boolReverseLeftMotor1 = false;
    private boolean boolReverseLeftMotor2 = false;
    private boolean boolReverseRightMotor1 = false;
    private boolean boolReverseRightMotor2 = false;

    private String baseName = "Default";

    private String xmlFileLocation = "";
    private String xmlFileName = "";

    @Deprecated
    public LibraryXMLRobotSetUp(String location, String fileName){
        this.xmlFileLocation = location;
        this.xmlFileName = fileName;
    }

    public LibraryXMLRobotSetUp(){
        this.xmlFileLocation = "/home/lvuser/Sequences";
        this.xmlFileName = "SetUp.xml";
    }

    public void run(){
        File stepFile = new File(this.xmlFileLocation, this.xmlFileName);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(stepFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nStepList = doc.getElementsByTagName("Step");
            System.out.println("Step elements :" + nStepList.getLength());

            for (int i = 0; i < nStepList.getLength(); i++){
                Node step = nStepList.item(i);
                System.out.println("Step elements :" + step.getNodeName());

                //if (step.getNodeType() == Node.ELEMENT_NODE){
                if (step.getNodeName().equalsIgnoreCase("Setup")) {
                    Element eElement = (Element) step;

                    this.dblMotorGearReduction = Double.parseDouble(getElementByTag(eElement, "MotorGearReduction"));
                    this.dblDriveGearReduction = Double.parseDouble(getElementByTag(eElement, "DriveGearReduction"));
                    this.dblWheelDiameter = Double.parseDouble(getElementByTag(eElement, "WheelDia"));
                    this.dblMaxVelocity = Double.parseDouble(getElementByTag(eElement, "MaxVelocity"));

                    this.boolReverseLeftMotor1 = Boolean.parseBoolean(getElementByTag(eElement, "ReverseLeftMotor1"));
                    this.boolReverseLeftMotor2 = Boolean.parseBoolean(getElementByTag(eElement, "ReverseLeftMotor2"));
                    this.boolReverseRightMotor1 = Boolean.parseBoolean(getElementByTag(eElement, "ReverseRightMotor1"));
                    this.boolReverseRightMotor2 = Boolean.parseBoolean(getElementByTag(eElement, "ReverseRightMotor2"));
                
                    this.baseName = getElementByTag(eElement, "BaseName");
                }

                if(step.getNodeName().equalsIgnoreCase("CAN_IDs")){
                    Element eElement = (Element) step;

                    this.mintLeftMotor1ID = Integer.parseInt(getElementByTag(eElement, "LeftMotor1ID"));
                    this.mintLeftMotor2ID = Integer.parseInt(getElementByTag(eElement, "LeftMotor2ID"));
                    this.mintRightMotor1ID = Integer.parseInt(getElementByTag(eElement, "RightMotor1ID"));
                    this.mintRightMotor2ID = Integer.parseInt(getElementByTag(eElement, "RightMotor2ID"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getElementByTag(Element eElement, String tag){
        return eElement.getElementsByTagName(tag).item(0).getTextContent();
    }
}