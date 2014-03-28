#include "cute.h"
#include "ide_listener.h"
#include "cute_runner.h"
#include "Circle.h"
#include "Diamond.h"
#include "Ellipse.h"
#include "Rectangle.h"
#include "Square.h"
#include "Triangle.h"

void circlePegs() {
	Circle obj(8);
	ASSERT_EQUAL(2, obj.pegs());
}

void circleSeeds() {
	Circle obj(8);
	ASSERT_EQUAL_DELTA(20.1062, obj.seeds(), 0.0001);
}

void circleRopes() {
	Circle obj(8);
	ASSERT_EQUAL_DELTA(58.2655, obj.ropes(),0.0001);
}

void circleError() {
	ASSERT_THROWSM("Error: positive parameter expected!", Circle (-1);, std::out_of_range);
}

void diamondPegs() {
	Diamond obj(8, 8, 60);
	ASSERT_EQUAL(4, obj.pegs());
}

void diamondSeeds() {
	Diamond obj(8, 8, 60);
	ASSERT_EQUAL_DELTA(5.54256, obj.seeds(), 0.0001);
}

void diamondRopes() {
	Diamond obj(8, 8, 60);
	ASSERT_EQUAL_DELTA(32, obj.ropes(),0.0001);
}

void diamondError_1() {
	ASSERT_THROWSM("Error: positive parameter expected!", Diamond (-1, 2, 60);, std::out_of_range);
}

void diamondError_2() {
	ASSERT_THROWSM("Error: positive parameter expected!", Diamond (8, -1, 60);, std::out_of_range);
}

void diamondError_3() {
	ASSERT_THROWSM("Error: positive parameter expected!", Diamond (8, 8, -1);, std::out_of_range);
}

void diamondError_4() {
	ASSERT_THROWSM("Error: angle has to be less then 180!", Diamond (8, 8, 180);, std::out_of_range);
}

void ellipsePegs() {
	Ellipse obj(8, 8);
	ASSERT_EQUAL(3, obj.pegs());
}

void ellipseSeeds() {
	Ellipse obj(8, 8);
	ASSERT_EQUAL_DELTA(20.1062, obj.seeds(), 0.0001);
}

void ellipseRopes() {
	Ellipse obj(8, 8);
	ASSERT_EQUAL_DELTA(66.2655, obj.ropes(),0.0001);
}

void ellipseError_1() {
	ASSERT_THROWSM("Error: positive parameter expected!", Ellipse (-1, 8);, std::out_of_range);
}

void ellipseError_2() {
	ASSERT_THROWSM("Error: positive parameter expected!", Ellipse (8, -1);, std::out_of_range);
}

void rectanglePegs() {
	Rectangle obj(8, 8);
	ASSERT_EQUAL(4, obj.pegs());
}

void rectangleSeeds() {
	Rectangle obj(8, 8);
	ASSERT_EQUAL_DELTA(6.4, obj.seeds(), 0.0001);
}

void rectangleRopes() {
	Rectangle obj(8, 8);
	ASSERT_EQUAL_DELTA(128, obj.ropes(),0.0001);
}

void rectangleError_1() {
	ASSERT_THROWSM("Error: positive parameter expected!", Rectangle (-1, 8);, std::out_of_range);
}

void rectangleError_2() {
	ASSERT_THROWSM("Error: positive parameter expected!", Rectangle (8, -1);, std::out_of_range);
}

void squarePegs() {
	Square obj(8);
	ASSERT_EQUAL(4, obj.pegs());
}

void squareSeeds() {
	Square obj(8);
	ASSERT_EQUAL_DELTA(6.4, obj.seeds(), 0.0001);
}

void squareRopes() {
	Square obj(8);
	ASSERT_EQUAL_DELTA(32, obj.ropes(),0.0001);
}

void squareError_1() {
	ASSERT_THROWSM("Error: positive parameter expected!", Square (-1);, std::out_of_range);
}

void trianglePegs() {
	Triangle obj(8, 8, 8);
	ASSERT_EQUAL(3, obj.pegs());
}

void triangleSeeds() {
	Triangle obj(8, 8, 8);
	ASSERT_EQUAL_DELTA(2.77128, obj.seeds(), 0.0001);
}

void triangleRopes() {
	Triangle obj(8, 8, 8);
	ASSERT_EQUAL_DELTA(24, obj.ropes(),0.0001);
}

void triangleError_1() {
	ASSERT_THROWSM("Error: positive parameter expected!", Triangle (-1, 8, 8);, std::out_of_range);
}

void triangleError_2() {
	ASSERT_THROWSM("Error: positive parameter expected!", Triangle (8, -1, 8);, std::out_of_range);
}

void triangleError_3() {
	ASSERT_THROWSM("Error: positive parameter expected!", Triangle (8, 8, -1);, std::out_of_range);
}

void triangleError_4() {
	ASSERT_THROWSM("Error: no triangle exists with sides 1, 2, 3 because 1 + 2 <= 3", Triangle (1, 2, 3);, std::out_of_range);
}

void runSuite(){
	cute::suite s;
	s.push_back(CUTE(circlePegs));
	s.push_back(CUTE(circleSeeds));
	s.push_back(CUTE(circleRopes));
	s.push_back(CUTE(circleError));
	s.push_back(CUTE(diamondPegs));
	s.push_back(CUTE(diamondSeeds));
	s.push_back(CUTE(diamondRopes));
	s.push_back(CUTE(diamondError_1));
	s.push_back(CUTE(diamondError_2));
	s.push_back(CUTE(diamondError_3));
	s.push_back(CUTE(diamondError_4));
	s.push_back(CUTE(ellipsePegs));
	s.push_back(CUTE(ellipseSeeds));
	s.push_back(CUTE(ellipseRopes));
	s.push_back(CUTE(ellipseError_1));
	s.push_back(CUTE(ellipseError_2));
	s.push_back(CUTE(rectanglePegs));
	s.push_back(CUTE(rectangleSeeds));
	s.push_back(CUTE(rectangleRopes));
	s.push_back(CUTE(rectangleError_1));
	s.push_back(CUTE(rectangleError_2));
	s.push_back(CUTE(squarePegs));
	s.push_back(CUTE(squareSeeds));
	s.push_back(CUTE(squareRopes));
	s.push_back(CUTE(squareError_1));
	s.push_back(CUTE(trianglePegs));
	s.push_back(CUTE(triangleSeeds));
	s.push_back(CUTE(triangleRopes));
	s.push_back(CUTE(triangleError_1));
	s.push_back(CUTE(triangleError_2));
	s.push_back(CUTE(triangleError_3));
	s.push_back(CUTE(triangleError_4));
	cute::ide_listener lis;
	cute::makeRunner(lis)(s, "The Suite");
}

int main(){
    runSuite();
}
