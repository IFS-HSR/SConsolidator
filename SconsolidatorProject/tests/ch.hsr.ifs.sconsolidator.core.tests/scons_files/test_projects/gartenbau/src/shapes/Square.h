#ifndef SQUARE_H_
#define SQUARE_H_

#include "Shape.h"

#include <string>

class Square: public Shape {
public:
	explicit Square(double s);
	unsigned int pegs() const;
	double ropes() const;
	double seeds() const;
	std::string name() const;
private:
	double s;
};

#endif /* SQUARE_H_ */
